package cn.jboost.springboot.autoconfig.alimns.executor.task.receive;

import cn.jboost.springboot.autoconfig.alimns.executor.MessageDto;
import cn.jboost.springboot.autoconfig.alimns.executor.MnsExecutor;
import cn.jboost.springboot.autoconfig.alimns.listener.ConsumeMessageCallback;
import cn.jboost.springboot.autoconfig.alimns.listener.MnsListener;
import cn.jboost.springboot.common.jackson.JsonUtil;
import com.aliyun.mns.model.BaseMessage;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public abstract class AbstractReceiveTaskExecutor<T extends BaseMessage> implements ReceiveTaskExecutor {

	protected final MnsListener mnsListener;

	protected final MnsExecutor mnsExecutor;

	protected AbstractReceiveTaskExecutor(MnsListener mnsListener, MnsExecutor mnsExecutor) {
		this.mnsListener = mnsListener;
		this.mnsExecutor = mnsExecutor;
	}

	protected void consumeMessage(T message, ConsumeMessageCallback<T> consumeMessageCallback) {
		String messageBody = consumeMessageCallback.getMessageBody(message);
		MessageDto messageDto = createReceivedMessage(messageBody);
		mnsExecutor.afterSuccessReceive(messageDto, mnsListener, message, consumeMessageCallback);

	}

	private MessageDto createReceivedMessage(String messageBody) {
		MessageDto messageDto;
		try {
			Map<String, Object> map = JsonUtil.parseMap(messageBody);
			String id = MapUtils.getString(map, "_id");
			String messageTxt = MapUtils.getString(map, "content");
			messageDto = new MessageDto(id, mnsListener.getMnsRef(), messageTxt);
			return messageDto;
		} catch (Exception ex) {
			messageDto = new MessageDto(MessageDto.BAD_MESSAGE_ID, mnsListener.getMnsRef(), messageBody);
		}
		return messageDto;

	}


}
