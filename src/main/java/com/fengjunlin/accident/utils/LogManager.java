package com.fengjunlin.accident.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志输出工具，在配置文件中配置存入log文件的输出级别，此处的信息即可存入文件
 */
public class LogManager {

    /**
     * 常规数据，日志输出<br>
     * 示例: LogManage.error(message, e,this.getClass(),LogLevel.ERROR);
     *
     * @param message 需要记录到日志的文本内容
     * @param clazz   本类class对象，一般是this.getClass()
     * @param sign    日志级别，一般设置ERROR,WARN,INFO,DEBUG
     *                4个级别,后面的级别包含前面，例如设置info，则不会输出debug级别的日志
     */
    public static <T> void normal(String message, Class<T> clazz, LogLevel sign) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (sign) {
            case WARN:
                logger.warn(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

    /**
     * 错误日志输出 <br>
     * 示例: LogManage.error(e,this.getClass(),LogLevel.ERROR);
     *
     * @param throwable 异常类
     * @param clazz     本类class对象，一般是this.getClass()
     */
    public static <T> void error(Throwable throwable, Class<T> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(throwable.getMessage(), throwable);
    }

}
