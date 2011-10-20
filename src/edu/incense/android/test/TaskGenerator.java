/**
 * 
 */
package edu.incense.android.test;

import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import edu.incense.android.datatask.model.Task;
import edu.incense.android.datatask.model.TaskType;
import edu.incense.android.datatask.trigger.Condition;
import edu.incense.android.datatask.trigger.JsonTrigger;
import edu.incense.android.sensor.AccelerometerSensor;

/**
 * @author mxpxgx
 *
 */
public class TaskGenerator {
    
    public static Task createTask(ObjectMapper mapper, String name, TaskType type){
        Task task = new Task();
        task.setName(name);
        task.setTaskType(type);
        return task;
    }
    
    public static Task createTask(ObjectMapper mapper, String name, TaskType type, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, name, type);
        task.setSampleFrequency(sampleFrequency); 
        return task;
    }
    
    public static Task createTaskWithPeriod(ObjectMapper mapper, String name, TaskType type, long period){
        Task task = TaskGenerator.createTask(mapper, name, type);
        task.setPeriodTime(period);
        return task;
    }

    
    public static Task createGpsSensor(ObjectMapper mapper, long period){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "GpsSensor", TaskType.GpsSensor, period);
        return task;
    }
    
    public static Task createTimerSensor(ObjectMapper mapper, long sourcePeriod, long sensorPeriod){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "TimerSensor", TaskType.TimerSensor, sourcePeriod);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("period", sensorPeriod); 
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createAudioSensor(ObjectMapper mapper, int sampleFrequency, long duration){
        Task task = TaskGenerator.createTask(mapper, "AudioSensor", TaskType.AudioSensor, sampleFrequency);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put("duration", duration);
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createNfcSensor(ObjectMapper mapper, int sampleFrequency){
        Task task = TaskGenerator.createTask(mapper, "NfcSensor", TaskType.NfcSensor, sampleFrequency);
        return task;
    }
    
    public static Task createAccelerometerSensor(ObjectMapper mapper, int sampleFrequency, long frameTime, long duration){
        Task task = TaskGenerator.createTask(mapper, "AccelerometerSensor", TaskType.AccelerometerSensor, sampleFrequency);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put(AccelerometerSensor.ATT_FRAMETIME, frameTime);
        ((ObjectNode) extrasNode).put(AccelerometerSensor.ATT_DURATION, duration);
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createWifiConnectionSensor(ObjectMapper mapper, long period, String[] ap){
        Task task = TaskGenerator.createTaskWithPeriod(mapper, "WifiConnectionSensor", TaskType.WifiConnectionSensor, period);
        JsonNode accessPoints = mapper.createObjectNode();
        ArrayNode array = ((ObjectNode) accessPoints).putArray("accessPoints");
        for(int i=0; i<ap.length; i++){
            array.add(ap[i]);
        }
        task.setJsonNode(accessPoints);
        return task;
    }
    
    private static Task createTrigger(ObjectMapper mapper, String name, TaskType type, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTaskWithPeriod(mapper, name, type, period);
        JsonNode extrasNode = mapper.createObjectNode();
        ((ObjectNode) extrasNode).put(JsonTrigger.MATCHES, matches);
        JsonNode conditionsNode = mapper.valueToTree(conditions);
        ((ObjectNode) extrasNode).put(JsonTrigger.CONDITIONS, conditionsNode);
        task.setJsonNode(extrasNode);
        return task;
    }
    
    public static Task createTrigger(ObjectMapper mapper, String name, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTrigger(mapper, name, TaskType.Trigger, period, matches, conditions);
        return task;
    }
    
    public static Task createStopTrigger(ObjectMapper mapper, String name, long period, String matches, ArrayList<Condition> conditions){
        Task task = createTrigger(mapper, name, TaskType.StopTrigger, period, matches, conditions);
        return task;
    }
    
    public static Condition createCondition(String...p){
        Condition c = new Condition();
        if(p.length > 0 && p[0]!=null){
            c.setData(p[0]);
        }
        if(p.length > 1 && p[1]!=null){
            c.setType(p[1]);
        }
        if(p.length > 2 && p[2]!=null){
            c.setOperator(p[2]);
        }
        if(p.length > 3 && p[3]!=null){
            c.setOperator(p[2]);
        }
        if(p.length > 4 && p[4]!=null){
            c.setValue1(p[4]);
        }
        if(p.length > 5 && p[5]!=null){
            c.setValue2(p[5]);
        }
        return c;
    }
    
    


}