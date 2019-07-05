package co.acaia.communications.reliableQueue;

import co.acaia.communications.CommLogger;

/**
 * Created by hanjord on 15/3/26.
 */
public class ReliableJob {
    private int jobId=0;
    private long enqueued_time=0;
    private int retry=0;
    private byte [] sendData;
    private Boolean if_high_priority=false;
    public ReliableJob(long enqueued_time_,byte[] sendData_,int jobId_,Boolean priority){
        enqueued_time=enqueued_time_;
        sendData=sendData_.clone();
        jobId=jobId_;
        if_high_priority=priority;
        retry= ReliableQueueServiceSettings.retryNum;
    }
    public long getEnqueued_time(){
        return enqueued_time;
    }

    public Boolean if_high_priority(){
        return if_high_priority;
    }

    public Boolean shouldDie(){
        CommLogger.logv("ReliableJob ttl", String.valueOf((System.nanoTime() - enqueued_time) / 1000000.0));
        if((System.nanoTime()-enqueued_time)/1000000.0>= ReliableQueueServiceSettings.wait_len){
            return true;
        }else{
            return false;
        }
    }

    public Boolean shouldSend(){
        if(retry>0){
            return true;
        }else{
            return false;
        }
    }

    public void doneRetry(){
        retry--;
    }

    public byte[] getData(){
        if(retry>0){
            retry--;
        }
        return sendData;
    }

}
