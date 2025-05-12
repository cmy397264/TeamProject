package com.example.businessreportgenerator.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/* ───────────────────── object 내부 ───────────────────── */
object NotificationHelper {

    private const val CHANNEL_ID   = "report_channel"
    private const val CHANNEL_NAME = "Business Report"

    /* ---------- 1) 즉시 테스트 알림 ---------- */
    fun sendTestNotification(
        context: Context,
        title: String = "테스트 알림",
        body:  String = "알림이 정상적으로 작동합니다.",
    ) {
        if (!hasPostPermission(context)) return
        createChannel(context)

        // id 동일하게 하면 계속 같은 id로 인식하기에 매번 알람을 다르게 뜨게 하기 위해 요렇게 해주기
        val id = System.currentTimeMillis().toInt()

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)   // TODO: 교체 가능
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(id, notif)
    }

    /* ---------- 2) 정확(Doze 무시) 주간 알림 ---------- */
    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleExactWeekly(
        context: Context,
        id: Int,
        title: String,
        body: String,
        dayOfWeek: DayOfWeek,
        hour: Int,
        minute: Int,
    ) {
        createChannel(context)
        val triggerAt = nextTrigger(dayOfWeek, hour, minute)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("body", body)
            putExtra("dayOfWeek", dayOfWeek.value)
            putExtra("hour", hour)
            putExtra("minute", minute)
        }
        val pending = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }

    /* ---------- 3) WorkManager 주간 알림 (±15 분) ---------- */
    fun schedulePeriodicWeekly(
        context: Context,
        id: Int = 9999,
        title: String,
        body: String,
        dayOfWeek: DayOfWeek,
        hour: Int,
        minute: Int,
    ) {
        createChannel(context)
        val delay = nextTrigger(dayOfWeek, hour, minute) - System.currentTimeMillis()

        val data = workDataOf("id" to id, "title" to title, "body" to body)
        val request = PeriodicWorkRequestBuilder<WeeklyNotificationWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weekly_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    /* ---------- 4) 외부에서도 쓸 수 있는 공통 발사 ---------- */
    fun showNotification(context: Context, id: Int, title: String, body: String) {
        if (!hasPostPermission(context)) return
        createChannel(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(
            id,
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()
        )
    }

    /* ---------- 내부 util ---------- */
    private fun hasPostPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                mgr.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH // 창에서 바로 보이게
                    ).apply {
                        enableVibration(true)
                        enableLights(true)
                    }
                )
            }
        }
    }

    private fun nextTrigger(dow: DayOfWeek, hour: Int, minute: Int): Long {
        val now  = LocalDateTime.now()
        var next = now.withHour(hour).withMinute(minute)
            .withSecond(0).withNano(0)
        while (next.dayOfWeek != dow || next.isBefore(now)) next = next.plusDays(1)
        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}   // ← object 끝 지점! 반드시 마지막 한 줄

/* ───────────────────── object 바깥 영역 ───────────────────── */
class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        val id    = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title") ?: return
        val body  = intent.getStringExtra("body") ?: ""
        NotificationHelper.showNotification(context, id, title, body)

        // 다음 주 재등록
        val dow    = DayOfWeek.of(intent.getIntExtra("dayOfWeek", 1))
        val hour   = intent.getIntExtra("hour", 9)
        val minute = intent.getIntExtra("minute", 0)
        NotificationHelper.scheduleExactWeekly(context, id, title, body, dow, hour, minute)
    }
}

class WeeklyNotificationWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val id    = inputData.getInt("id", 9999)
        val title = inputData.getString("title") ?: return Result.failure()
        val body  = inputData.getString("body") ?: ""
        NotificationHelper.showNotification(applicationContext, id, title, body)
        return Result.success()
    }
}
