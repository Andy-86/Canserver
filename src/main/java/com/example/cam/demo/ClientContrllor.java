package com.example.cam.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import io.swagger.annotations.ApiOperation;

@RestController
//@Api(tags = "1.1", description = "发送can信息", value = "发送can信息")
public class ClientContrllor {
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "发送id", dataType = ApiDataType.INT, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "led0", value = "led0", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "led1", value = "led1", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "led2", value = "led2", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "led3", value = "led3", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "gauge0", value = "gauge0", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "gauge1", value = "gauge1", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "gauge2", value = "gauge2", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//            @ApiImplicitParam(name = "gauge3", value = "gauge3", dataType = ApiDataType.BYTE, paramType = ApiParamType.QUERY),
//    })
    @ApiOperation(value = "添加can信息",notes = "要输入一个12byte的can信息")
    @PostMapping("/upcan")
    public boolean UploadCan(@RequestParam("id") Integer id,@RequestParam("led0") byte led0,@RequestParam("led1") byte led1,
                             @RequestParam("led2") byte led2,@RequestParam("led3") byte led3,@RequestParam("gauge0") byte gauge0,
                             @RequestParam("gauge1") byte gauge1,@RequestParam("gauge2") byte gauge2,@RequestParam("gauge3") byte gauge3){
        System.out.println(" "+id);
        try {
            byte[] can=new byte[12];
            byte[] cid=new byte[4];
            cid=DataUtil.intToByte(id);
            System.arraycopy(cid,0,can,0,4);
            can[4]=led0;
            can[5]=led1;
            can[6]=led2;
            can[7]=led3;
            can[8]=gauge0;
            can[9]=gauge1;
            can[10]=gauge2;
            can[11]=gauge3;
            BlockQueuePool.getInstance().queue.put(new ByteTaker(can));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
    @PostMapping(value = "/test",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean testList(@RequestBody String[] list){
        if(list==null){
            return false;
        }else {
            if(list.length>10)
                return false;
            byte[][] cans=new byte[list.length][12];
            int i=0;
            int index=0;
            for(String s:list) {
                    System.out.println(" "+AnalysisUtil.byteArrayToHexStr(AnalysisUtil.hexStrToByteArray(s)));
                    byte[] can=AnalysisUtil.hexStrToByteArray(s);
                    if(can.length==12){
                        cans[index]=can;
                        index++;
                    }
                    i++;
            }
            byte[] command=new byte[12*index+1];
            command[0]=(byte) index;
            for(int j=0;j<index;j++){
                System.arraycopy(cans[j],0,command,1+12*j,12);
            }
                BlockQueuePool.getInstance().queue.offer(new ByteTaker(command));
            return true;
        }
    }

    @RequestMapping("/getcans")
    public ArrayList<Can> getCans(){
        ArrayList<Can> list=new ArrayList<>();
        int i=0;
        for(byte[] can:BlockQueuePool.getInstance().cans){
            Can taker=new Can();
            taker.setId(i++);
            taker.setCan(AnalysisUtil.byteArrayToHexStr(can));
            list.add(taker);
        }
        return list;
    }
}
