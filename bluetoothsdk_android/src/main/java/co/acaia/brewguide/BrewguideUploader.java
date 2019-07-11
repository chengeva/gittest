package co.acaia.brewguide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import co.acaia.brewguide.events.BrewguideCommandEvent;
import co.acaia.brewguide.events.BrewguideInfoEvent;
import co.acaia.brewguide.events.BrewguideScaleEvent;
import co.acaia.brewguide.events.BrewguideStepEvent;
import co.acaia.brewguide.events.BrewguideStringEvent;
import co.acaia.brewguide.events.PearlSUploadProgressEvent;
import co.acaia.brewguide.model.BrewStep;
import co.acaia.brewguide.model.Brewguide;
import co.acaia.communications.protocol.ver20.BrewguideProtocol;
import co.acaia.communications.scaleService.gatt.Log;

public class BrewguideUploader {

    public static enum UPLOAD_MODE
    {
        upload_mode_brewguide,
        upload_mode_hello,
    };

    public UPLOAD_MODE upload_mode;
    private Brewguide brewguide;
    private int totalBrewguideSteps;

    private ArrayList<BrewguideProtocol.new_brewguide_data_step> brewguide_data_steps;

    private ArrayList<ArrayList<BrewguideProtocol.new_brewguide_data_string>> brewguide_data_strings;
    private ArrayList<ArrayList<BrewguideProtocol.new_brewguide_data_string>>brewguide_data_steps_title;

    public BrewguideUploader(UPLOAD_MODE upload_mode_){
        EventBus.getDefault().register(this);
        this.upload_mode=upload_mode_;

    }

    /**
     * Refacture this structure later.
     */
    public void setHelloData(String helloData){
        brewguide_data_steps_title=new ArrayList<>();
        ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step=new ArrayList<>();
        //BrewguideProtocol.new_brewguide_data_string titleBrewguideString=new BrewguideProtocol.new_brewguide_data_string();
        constructStringWithData(brewguide_data_string_step,helloData,(short)0);
        brewguide_data_steps_title.add(brewguide_data_string_step);
    }

    public void setBrewguideData(Brewguide brewguideData){
        if(this.upload_mode==UPLOAD_MODE.upload_mode_brewguide) {
            this.brewguide = brewguideData;
            brewguide_data_steps = new ArrayList<>();
            brewguide_data_steps_title = new ArrayList<>();
            brewguide_data_strings = new ArrayList<>();
            constructSteps();
            constructStrings();
            Log.v("BrewguideUploader", "brewguide_data_strings size= " + String.valueOf(brewguide_data_strings.size()));
        }
    }

    private void constructStrings()
    {


        // Title
        ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step=new ArrayList<>();
        //BrewguideProtocol.new_brewguide_data_string titleBrewguideString=new BrewguideProtocol.new_brewguide_data_string();
        constructStringWithData(brewguide_data_string_step,"Title",(short)31);
        brewguide_data_steps_title.add(brewguide_data_string_step);
        // Roaster
        ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step_roaster=new ArrayList<>();
        //BrewguideProtocol.new_brewguide_data_string titleBrewguideString=new BrewguideProtocol.new_brewguide_data_string();
        constructStringWithData(brewguide_data_string_step_roaster,"Title",(short)56);
        brewguide_data_steps_title.add(brewguide_data_string_step_roaster);
        // Steps
        for(int i=0;i!=brewguide.getBrewSteps().size();i++){
            ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step_step=new ArrayList<>();
            constructStringWithData(brewguide_data_string_step_step,brewguide.getBrewSteps().get(i).message,(short)(81+i*25));
            brewguide_data_strings.add(brewguide_data_string_step_step);
            totalBrewguideSteps=86+20+i*25;
        }

        for(int i=brewguide.getBrewSteps().size();i!=30;i++){
            ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step_step=new ArrayList<>();
            if(brewguide.getBrewSteps().size()>0){
                constructStringWithData(brewguide_data_string_step_step,brewguide.getBrewSteps().get(0).message,(short)(81+i*25));
            }else {
                constructStringWithData(brewguide_data_string_step_step,"",(short)(81+i*25));
            }
            brewguide_data_strings.add(brewguide_data_string_step_step);

        }
        // 206

    }

    private void constructStringWithData(
            ArrayList<BrewguideProtocol.new_brewguide_data_string> brewguide_data_string_step,
            String str_data,
            short startPage){

        /**
         *     result=[ori_title stringByReplacingOccurrencesOfString:@"\n" withString:@""];
         *     result=[ori_title stringByReplacingOccurrencesOfString:@" " withString:@"`"];
         *     result=[result stringByReplacingOccurrencesOfString:@"\n" withString:@" "];
         */
        // nsStr= [nsStr stringByReplacingOccurrencesOfString:@":)"
        //                                            withString:@"&"];
        // Remove test code for 1.0.023
        //if(str_data.length()==39){
        //    str_data+=" ";
        //}
        str_data=str_data.replace(" ","`");
        str_data=str_data.replace("\n"," ");
        str_data=str_data.replace(":)","&");

        //nsStr= [nsStr stringByReplacingOccurrencesOfString:@":)"
        //withString:@"&"];

        char[] char_array=str_data.toCharArray();
        char charPerPacket=8;
        char currCharPt=0;
        int currPacketPt=1;
        int str_lenth=200;


        if(str_data.length()<200){
            str_lenth=str_data.length();
        }
        Log.v("BrewguideUploader","Step string="+str_data+" "+String.valueOf(str_lenth));

        for(short i=0;i!=25;i++){
            BrewguideProtocol.new_brewguide_data_string brewguide_data_string=new BrewguideProtocol.new_brewguide_data_string();
            // brewguide_data_string_step
            short currPage=(short)(i+startPage);
            brewguide_data_string.set_pageid(currPage);
            brewguide_data_string_step.add(brewguide_data_string);
            //SLog(@"roaster got page id=%d",currDataStr->page_id);
        }

        for(int i=0;i<=str_lenth;i++){
            BrewguideProtocol.new_brewguide_data_string brewguide_data_string=brewguide_data_string_step.get(currPacketPt);
            brewguide_data_string.set_data_len((short)(currCharPt+1));
            brewguide_data_string.set_string_id((short)currPacketPt);
            short chara=(short)'\0';
            if(i!=str_lenth){
                chara=(short)char_array[i];
            }else{
                Log.v("BrewguideUploader","end char");
            }

            //Log.v("BrewguideUploader","chara="+String.valueOf(chara));
            if(chara=='&'){
                chara=128;
            }
            switch (currCharPt) {
                case 0:
                    brewguide_data_string.string0.set(chara);
                    break;
                case 1:
                    brewguide_data_string.string1.set(chara);
                    break;
                case 2:
                    brewguide_data_string.string2.set(chara);
                    break;
                case 3:
                    brewguide_data_string.string3.set(chara);
                    break;
                case 4:
                    brewguide_data_string.string4.set(chara);
                    break;
                case 5:
                    brewguide_data_string.string5.set(chara);
                    break;
                case 6:
                    brewguide_data_string.string6.set(chara);
                    break;
                case 7:
                    brewguide_data_string.string7.set(chara);
                    break;
            }
            /*if(str[i]=='&'){
                DLog(@"Replace smily");
                currDataStr->string[currCharPt]=128;
            }else{
                currDataStr->string[currCharPt]=str[i];
            }*/
            //NSLog(@"page_id=%d",currDataStr->page_id);
            currCharPt++;

            if(currPacketPt <= 25 && currCharPt>=charPerPacket){
                currPacketPt++;
                currCharPt=0;
            }else if(currPacketPt >25){
                break;
            }
        }

        for(int i=0;i!=25;i++){
            BrewguideProtocol.new_brewguide_data_string brewguide_data_string=brewguide_data_string_step.get(i);
            brewguide_data_string.num_str.set((short)(str_lenth));
            Log.v("BrewguideUploader","brewguide_data_string.num_str "+String.valueOf(brewguide_data_string.num_str));
        }


    }

    private void constructSteps()
    {
        ArrayList<BrewStep> brewSteps=brewguide.getBrewSteps();
        for(int i=0;i!=brewSteps.size();i++){

            BrewStep brewStep=brewSteps.get(i);
            Log.v("BrewguideUploader","constructing step:"+String.valueOf(i)+" "+brewStep.message+" "+String.valueOf(brewStep.weight)+" type="+String.valueOf(brewStep.brewStepType)+" sub="+String.valueOf(brewStep.brewStepSubType));
            // short step_page_id,short time, short water, short pause1, short pause2, short sound1, short sound2, short stepType,short subType)
            // Fix decimal issue
            if(brewStep.brewStepSubType==1 && brewStep.brewStepType==0){
                //coffee amount step
            }else {
                brewStep.weight/=10;
            }
            BrewguideProtocol.new_brewguide_data_step brewguide_data_step=new BrewguideProtocol.new_brewguide_data_step(
                    (short)(brewStep.index+1),
                    (short)brewStep.time,
                    (short)brewStep.weight,
                    (short)brewStep.autoPause0,
                    (short)brewStep.autoPause1,
                    (short) brewStep.alertSound0,
                    (short) brewStep.alertSound1,
                    (short) brewStep.brewStepType,
                    (short)brewStep.brewStepSubType
            );
            brewguide_data_steps.add(brewguide_data_step);
        }

        for(int i=brewSteps.size();i!=30;i++){
            //BrewStep brewStep=brewSteps.get(0);
            // short step_page_id,short time, short water, short pause1, short pause2, short sound1, short sound2, short stepType,short subType)
            BrewguideProtocol.new_brewguide_data_step brewguide_data_step=new BrewguideProtocol.new_brewguide_data_step(
                    (short)(i+1),
                    (short)0,
                    (short)0,
                    (short)0,
                    (short)0,
                    (short) 0,
                    (short) 0,
                    (short) 0,
                    (short)0
            );
            brewguide_data_steps.add(brewguide_data_step);
        }
    }

    private void notifyStep(int currentPage){
        int progress=(int)(100*(((float)currentPage))/25.0f);
        EventBus.getDefault().post(new PearlSUploadProgressEvent(progress));
    }

    private void notifyStepBrewguide(int currentPage){
        // [self notifyUploadStep:((int)100*(((float)brewguideDAT))/total_steps)];
        Log.v("Brewguide uploader:","Progress "+String.valueOf(currentPage));
        int progress=(int)(100*(((float)currentPage))/totalBrewguideSteps);
        EventBus.getDefault().post(new PearlSUploadProgressEvent(progress));
    }

    /**
     * Hanjord: Brewguide message handler
     */
    @Subscribe
    public void OnEvent(BrewguideScaleEvent brewguideScaleEvent)
    {

        int dataIndex = brewguideScaleEvent.command_val;
        co.acaia.communications.scaleService.gatt.Log.v("BrewguideUploader", "Got event from scale:" + String.valueOf(brewguideScaleEvent.command_id));
        short brewguideCmd = brewguideScaleEvent.command_id;
        if (brewguideCmd == BrewguideProtocol.BREWGUIDE_CMD.brewguide_cmd_state.ordinal()) {
            // Process sync number of Brewguide pages

            if (this.upload_mode == UPLOAD_MODE.upload_mode_brewguide) {
                if(brewguide!=null) {
                    Log.v("BrewguideUploader", "brewguide.numOfBrewguidePages() " + String.valueOf(brewguide.numOfBrewguidePages()));
                    BrewguideCommandEvent brewguideCommandEvent = new BrewguideCommandEvent((short) BrewguideProtocol.BREWGUIDE_CMD.brewguide_cmd_app_page_len.ordinal(), brewguide.numOfBrewguidePages());
                    EventBus.getDefault().post(brewguideCommandEvent);
                }
            }
            if(this.upload_mode==UPLOAD_MODE.upload_mode_hello){
                BrewguideCommandEvent brewguideCommandEvent = new BrewguideCommandEvent((short) BrewguideProtocol.BREWGUIDE_CMD.brewguide_cmd_app_page_len.ordinal(), (short)7);
                EventBus.getDefault().post(brewguideCommandEvent);
            }
        }

        if (brewguideCmd == BrewguideProtocol.BREWGUIDE_CMD.brewguide_cmd_request_page.ordinal()) {
            if (this.upload_mode == UPLOAD_MODE.upload_mode_brewguide) {
                if(brewguide!=null) {

                    Log.v("BrewguideUploader", "Scale want page " + String.valueOf(brewguideScaleEvent.command_val));
                    if (dataIndex == 0) {
                        // Process sync number of Brewguide pages

                        BrewguideProtocol.new_brewguide_data_info brewguide_data_info = new BrewguideProtocol.new_brewguide_data_info((short) 185, (short) 200, (short) 90);
                        EventBus.getDefault().post(new BrewguideInfoEvent(brewguide_data_info));
                    }
                    if (dataIndex >= 1 && dataIndex <= 30) {
                        // Process Sync Brewguide steps
                        Log.v("BrewguideUploader", "Upload step: " + String.valueOf(dataIndex)+" "+brewguide_data_steps.get(dataIndex - 1).getByteArray().toString());
                        EventBus.getDefault().post(new BrewguideStepEvent(brewguide_data_steps.get(dataIndex - 1)));
                    }
                    if (dataIndex >= 31 && dataIndex <= 55) {
                        BrewguideStringEvent brewguideStringEvent = new BrewguideStringEvent(brewguide_data_steps_title.get(0).get(dataIndex - 31));
                        EventBus.getDefault().post(brewguideStringEvent);
                    }

                    if (dataIndex >= 56 && dataIndex <= 80) {
                        BrewguideStringEvent brewguideStringEvent = new BrewguideStringEvent(brewguide_data_steps_title.get(1).get(dataIndex - 56));
                        EventBus.getDefault().post(brewguideStringEvent);
                    }

                    if (dataIndex >= 81) {
                        int brewguideIndex = dataIndex - 81;
                        int curr_dataIndex = brewguideIndex / 25;
                        int dataColIndex = dataIndex - 81 - curr_dataIndex * 25;
                        if(curr_dataIndex>29){
                            curr_dataIndex = 29;
                        }
                        Log.v("BrewguideUploader", "dataIndex=" + String.valueOf(brewguideIndex) + "curr_dataIndex " + String.valueOf(curr_dataIndex) + "dataColIndex " + String.valueOf(dataColIndex));
                        BrewguideStringEvent brewguideStringEvent = new BrewguideStringEvent(brewguide_data_strings.get(curr_dataIndex).get(dataColIndex));
                        EventBus.getDefault().post(brewguideStringEvent);
                    }

                    // Update Bewguide progress to UI
                    notifyStepBrewguide(dataIndex);
                }
            }
            if(this.upload_mode==UPLOAD_MODE.upload_mode_hello){
                notifyStep(dataIndex);
                if (dataIndex >= 0 && dataIndex < 25) {
                    // Process Sync Brewguide steps
                    Log.v("BrewguideUploader", "Upload step: " + String.valueOf(dataIndex));
                    BrewguideStringEvent brewguideStringEvent = new BrewguideStringEvent(brewguide_data_steps_title.get(0).get(dataIndex ));
                    EventBus.getDefault().post(brewguideStringEvent);
                }
            }
        }



    }

}
