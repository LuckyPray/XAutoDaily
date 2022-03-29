package me.teble.xposed.autodaily.task.cron.timingwheel

import me.teble.xposed.autodaily.utils.LogUtil
import java.util.function.Consumer


/**
 * 多层时间轮，常用于延时任务。<br></br>
 * 时间轮是一种环形数据结构，由多个槽组成，每个槽中存放任务集合。<br></br>
 * 一个单独的线程推进时间一槽一槽的移动，并执行槽中的任务。
 *
 * @author eliasyaoyc, looly
 */
class TimingWheel(
    /**
     * 一个时间槽的范围
     */
    private val tickMs: Long,
    /**
     * 时间轮大小，时间轮中时间槽的个数
     */
    private var wheelSize: Int, currentTime: Long, consumer: Consumer<TimerTaskList?>
) {
    /**
     * 时间跨度，当前时间轮总间隔，即单个槽的跨度*槽个数
     */
    private val interval: Long

    /**
     * 时间槽
     */
    private val timerTaskLists: Array<TimerTaskList?>

    /**
     * 当前时间，指向当前操作的时间格，代表当前时间
     */
    private var currentTime: Long

    /**
     * 上层时间轮
     */
    @Volatile
    private var overflowWheel: TimingWheel? = null

    /**
     * 任务处理器
     */
    private val consumer: Consumer<TimerTaskList?>

    /**
     * 构造
     *
     * @param tickMs    一个时间槽的范围，单位毫秒
     * @param wheelSize 时间轮大小
     * @param consumer  任务处理器
     */
    constructor(tickMs: Long, wheelSize: Int, consumer: Consumer<TimerTaskList?>) : this(
        tickMs,
        wheelSize,
        System.currentTimeMillis(),
        consumer
    ) {
    }

    /**
     * 添加任务到时间轮
     *
     * @param timerTask 任务
     * @return 是否成功
     */
    fun addTask(timerTask: TimerTask): Boolean {
        val expiration = timerTask.delayMs
        //过期任务直接执行
        if (expiration < currentTime + tickMs) {
            return false
        } else if (expiration < currentTime + interval) {
            //当前时间轮可以容纳该任务 加入时间槽
            val virtualId = expiration / tickMs
            val index = (virtualId % wheelSize).toInt()
            LogUtil.d("tickMs: $tickMs ------index: $index ------expiration: $expiration")
            var timerTaskList = timerTaskLists[index]
            if (null == timerTaskList) {
                timerTaskList = TimerTaskList()
                timerTaskLists[index] = timerTaskList
            }
            timerTaskList.addTask(timerTask)
            if (timerTaskList.setExpiration(virtualId * tickMs)) {
                //添加到delayQueue中
                consumer.accept(timerTaskList)
            }
        } else {
            //放到上一层的时间轮
            val timeWheel = getOverflowWheel()
            timeWheel!!.addTask(timerTask)
        }
        return true
    }

    /**
     * 推进时间
     *
     * @param timestamp 推进的时间
     */
    fun advanceClock(timestamp: Long) {
        if (timestamp >= currentTime + tickMs) {
            currentTime = timestamp - timestamp % tickMs
            if (overflowWheel != null) {
                //推进上层时间轮时间
                getOverflowWheel()!!.advanceClock(timestamp)
            }
        }
    }

    /**
     * 创建或者获取上层时间轮
     */
    private fun getOverflowWheel(): TimingWheel? {
        if (overflowWheel == null) {
            synchronized(this) {
                if (overflowWheel == null) {
                    overflowWheel = TimingWheel(interval, wheelSize, currentTime, consumer)
                }
            }
        }
        return overflowWheel
    }

    /**
     * 构造
     *
     * @param tickMs      一个时间槽的范围，单位毫秒
     * @param wheelSize   时间轮大小
     * @param currentTime 当前时间
     * @param consumer    任务处理器
     */
    init {
        wheelSize = wheelSize
        interval = tickMs * wheelSize
        timerTaskLists = arrayOfNulls(wheelSize)
        //currentTime为tickMs的整数倍 这里做取整操作
        this.currentTime = currentTime - currentTime % tickMs
        this.consumer = consumer
    }
}
