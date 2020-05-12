package com.fengjunlin.accident.config;

import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.config.ContainerProperties;

/**
 * 获取kafka的offset 提交方式
 */
public class KafkaOffsetAckMode {
  /**
   * 获取kafka offset 提交类型
   * @param offType 提交类型
   * @param ackTime 提交时间
   * @param ackCount 提交总数
   * @param containerProperties kafka 消费对象配置配置属性
   * @return
   */
  public static AckMode getAckMode(String offType,Long ackTime,int ackCount,ContainerProperties containerProperties) {
	  AckMode ackMode = AckMode.BATCH;
	  if("".equals(offType) || offType== null)  {return ackMode;}
	  switch (offType) {
	     /**
		 * Commit after each record is processed by the listener.
		 * 在侦听器处理每个记录之后提交
		 */
		case "RECORD":
			ackMode=AckMode.RECORD;
			break;
		/**
		 * Commit whatever has already been processed before the next poll.
		 * 提交在下次poll前已处理的内容
		 */
		case "BATCH":
			ackMode=AckMode.BATCH;
			break;
		/**
		 * Commit pending updates after
		 * 在之后提交挂起的更新
		 * {@link ContainerProperties#setAckTime(long) ackTime} has elapsed.
		 */
		case "TIME":
			ackMode=AckMode.TIME;
			if(ackTime >0) {containerProperties.setAckTime(ackTime);}
			break;
		/**
		 * Commit pending updates after
		 * {@link ContainerProperties#setAckCount(int) ackCount} has been
		 * exceeded.
		 */
		case "COUNT":
			ackMode=AckMode.COUNT;
			if(ackCount >0) {containerProperties.setAckCount(ackCount);}
			break;
		/**
		 * Commit pending updates after
		 * {@link ContainerProperties#setAckCount(int) ackCount} has been
		 * exceeded or after {@link ContainerProperties#setAckTime(long)
		 * ackTime} has elapsed.
		 */
		case "COUNT_TIME":
			ackMode=AckMode.COUNT_TIME;
			if(ackTime >0) {containerProperties.setAckTime(ackTime);}
			if(ackCount >0) {containerProperties.setAckCount(ackCount);}
			break;
		/**
		 * User takes responsibility for acks using an
		 * {@link AcknowledgingMessageListener}.
		 */
		case "MANUAL":
			ackMode=AckMode.MANUAL;
			break;
		/**
		 * User takes responsibility for acks using an
		 * {@link AcknowledgingMessageListener}. The consumer
		 * immediately processes the commit.
		 */
		case "MANUAL_IMMEDIATE":
			ackMode=AckMode.MANUAL_IMMEDIATE;
			break;
			
		default:
			//默认为
			break;
		}
	  return ackMode;
  }
}
