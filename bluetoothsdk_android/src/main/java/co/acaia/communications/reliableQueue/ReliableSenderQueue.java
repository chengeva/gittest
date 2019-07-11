package co.acaia.communications.reliableQueue;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import co.acaia.communications.CommLogger;
import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/3/26.
 */
public class ReliableSenderQueue {
    long lastSendTime = 0;
    public static final String TAG = "ReliableSenderQueue";
    private Timer sendTimer;
    Queue<ReliableJob> highPriorityJobQueue;
    Queue<ReliableJob> lowPriorityJobQueue;
    private ScaleCommunicationService mScaleCommunicationService;

    private ReliableJob currentJob = null;

    @Subscribe
    public void onEvent(SendSuccessEvent event) {
        if (currentJob != null) {
            CommLogger.logv(TAG, "took " + String.valueOf((System.nanoTime() - currentJob.getEnqueued_time()) / 1000000.0));
            releaseCurrentJob();
        }
    }

    public ReliableSenderQueue(ScaleCommunicationService mScaleCommunicationService_) {
        mScaleCommunicationService = mScaleCommunicationService_;

        highPriorityJobQueue = new LinkedList<ReliableJob>();
        lowPriorityJobQueue = new LinkedList<ReliableJob>();
        EventBus.getDefault().register(this);

        // sendTimer = new Timer();
       // sendTimer.schedule(new sendDataTask(), 200, ReliableQueueServiceSettings.send_interval);
    }

    public void sendHighPriorityJob(byte[] data, int jobid) {

        addHighPriorityJob(data, jobid);


    }

    public void sendLowPriorityJob(byte[] data, int jobid) {
        ReliableJob job = new ReliableJob(System.nanoTime(), data, jobid, false);
        sendJob(job, lowPriorityJobQueue);
    }

    private void addHighPriorityJob(byte[] data, int jobid) {
        highPriorityJobQueue.add(new ReliableJob(System.nanoTime(), data, jobid, true));
    }

    private void addLowPriorityJob(byte[] data, int jobid) {
        lowPriorityJobQueue.add(new ReliableJob(System.nanoTime(), data, jobid, false));
    }

    public void release() {
        sendTimer.cancel();
        sendTimer = null;
    }

    private synchronized void sendJob(ReliableJob reliableJob, Queue<ReliableJob> job_queue) {


        CommLogger.logv(TAG, "send data!");

        mScaleCommunicationService.sendCmdFromQueue(reliableJob.getData());
        lastSendTime = System.nanoTime();


    }

    private class sendDataTask extends TimerTask {

        @Override
        public void run() {
            try {
                if ((System.nanoTime() - lastSendTime) / 1000000.0 >= ReliableQueueServiceSettings.send_interval) {
                    if (highPriorityJobQueue.size() != 0) {
                        ReliableJob reliableJob = highPriorityJobQueue.poll();
                        sendJob(reliableJob, highPriorityJobQueue);
                    } else if (lowPriorityJobQueue.size() != 0) {
                        ReliableJob reliableJob = lowPriorityJobQueue.poll();
                        sendJob(reliableJob, lowPriorityJobQueue);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private synchronized Boolean isSendingData() {
        if (getCurrentJob() != null) {
            if (getCurrentJob().shouldDie()) {
                getCurrentJob().doneRetry();

                if (getCurrentJob().if_high_priority()) {
                    if (getCurrentJob().shouldSend()) {
                        highPriorityJobQueue.add(getCurrentJob());
                    }
                } else {
                    if (getCurrentJob().shouldSend()) {
                        lowPriorityJobQueue.add(getCurrentJob());
                    }
                }
                setCurrentJob(null);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    private synchronized ReliableJob getCurrentJob() {
        return currentJob;
    }

    private synchronized void setCurrentJob(ReliableJob job) {
        currentJob = job;
    }

    private synchronized void releaseCurrentJob() {
        currentJob = null;
    }

}
