package com.example.component;

import com.example.model.OutBoxEvent;
import com.example.repo.OutBoxEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class OutBoxEventRelayMessage {
    private final OutBoxEventRepo outBoxEventRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void relayMessage(){
        List<OutBoxEvent> events = outBoxEventRepo.findAllByIsFalse();
        for(OutBoxEvent event : events){
            try{
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                                .get(3, TimeUnit.SECONDS);
                event.setStatus(true);
                outBoxEventRepo.save(event);
            }catch(TimeoutException timeoutException){
                System.err.println("Kafka time out for event " + event.getTopic());
            }
            catch (Exception e){
                System.err.println("Failed to send message: " + event.getId());
            }
        }
    }
}
