package com.spdemo.service.erp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdemo.service.erp.dto.*;
import com.spdemo.service.jetstream.JetStreamService;

import io.nats.client.impl.Headers;
import io.nats.client.support.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class ErpConnectorServiceImpl implements ErpConnectorService {

    JetStreamService jetStreamService;

    public ErpConnectorServiceImpl(JetStreamService jetStreamService) {
        this.jetStreamService = jetStreamService;
    }

    @PostConstruct
    public void init() {
        // 註冊 subcribe

        // push
          // 如果使用 push Subscribe, 請將 if(false) 拿掉
            try {
                // nats jetstream 入口
                jetStreamService.pushSubscribe("EDI.Format.ToInternal.Transformed", (msg) -> {
                    String data = new String(msg.getData(), StandardCharsets.UTF_8);
                    ObjectMapper mapper = new ObjectMapper();

                    FormatToInternalTransformedEvent event = null;
                    try {
                        event = mapper.readValue(data, FormatToInternalTransformedEvent.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("轉換失敗 !", e);
                    }

                    try {
                        poResultUpdate(event);
                    } catch (Exception e) {
//                    log.error("poResultUpdate error !", e);
                        throw new IllegalStateException("poResultUpdate error !", e);
                    }
                });
            } catch (Exception e) {
                throw new IllegalStateException("無法註冊 Subscribe(" + "EDI.Format.ToInternal.Transformed" + ").");
            }



    }

    /**
     * pull 演示
     * 需要在 config object, 加上  @EnableScheduling
     */
    @Scheduled(fixedDelay = 500)
    public void pullSchedule1() throws Exception {
        String subjectName = "ERP.PO.Result.Updated";

        log.info("pullSubscribe " + subjectName);
        jetStreamService.pullSubscribe(subjectName, subjectName.replaceAll("\\.", "_") + "_default_pull_durable", 10, (msg) -> {
//        	Headers headers = msg.getHeaders();
//        	List<String> timestamp = headers.get("timestamp");
            String data = new String(msg.getData(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            EcPoResultUpdatedEvent event = null;
            try {
                event = mapper.readValue(data, EcPoResultUpdatedEvent.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("轉換失敗 !", e);
            }

            log.info(subjectName + ", 收到 event: \n" + JsonUtils.getFormatted(event));

            try {

                //TODO 接收後執行的程式碼
            } catch (Exception e) {
                // log.error("poResultUpdate error !", e);
                throw new IllegalStateException("poResultUpdate error !", e);
            }
        });
    }

    /**
     * 接受被選擇上傳的 PO
     */
    @Override
    public void poUploadIncoming(EcPoIncomingDto incomingDto) throws Exception {
        log.info("收到 event(EcPoIncomingDto), data: \n" + JsonUtils.getFormatted(incomingDto));

        // 處理 Po
        //// TODO


        // 產生事件
        EcPoUploadIncomingEvent event = new EcPoUploadIncomingEvent();
        event.setPoNumber(incomingDto.getPoNumber());
        event.setCreateAt(incomingDto.getCreateAt());
        String subjectName = "ERP.PO.Upload.Incoming";
        jetStreamService.publish(subjectName, null, event); // PO 待處理

    }

    /**
     * 以 PO 處理結果來更新 ERP 資料庫
     */
    @Override
    public void poResultUpdate(FormatToInternalTransformedEvent transformedEvent) throws Exception {

        log.info("收到 event(FormatToInternalTransformedEvent), data: \n" + JsonUtils.getFormatted(transformedEvent));
        // 以 PO 處理結果來更新 ERP 資料庫
        // TODO save data to database

        // 產生事件
        EcPoResultUpdatedEvent event = new EcPoResultUpdatedEvent();
        event.setPoNumber(transformedEvent.getPoNumber());
        event.setCreateAt(transformedEvent.getCreateAt());
        event.setStatus("SAVED");
        String subjectName = "ERP.PO.Result.Updated";

        jetStreamService.publish(subjectName, null, event);  // ERP 已更新收到的 PO 結果

    }


}
