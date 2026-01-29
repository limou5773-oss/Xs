package com.sxps.xsps.aop;

import com.sxps.xsps.mapper.MerchantAfterSaleLogMapper;
import com.sxps.xsps.pojo.AfterSale;
import com.sxps.xsps.pojo.AfterSaleLog;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AfterSaleLogAspect {

    private final MerchantAfterSaleLogMapper logMapper;

    @AfterReturning("@annotation(afterSaleLogRecord)")
    public void saveLog(JoinPoint jp, AfterSaleLogRecord afterSaleLogRecord) {

        Object[] args = jp.getArgs();

        Long afterSaleId = null;

        if (args.length > 0) {
            Object arg0 = args[0];

            // 情况 1：方法签名是 handle(Long id, Integer status)
            if (arg0 instanceof Long) {
                afterSaleId = (Long) arg0;
            }
            // 情况 2：方法签名是 apply(AfterSale afterSale)
            else if (arg0 instanceof AfterSale) {
                AfterSale afterSale = (AfterSale) arg0;
                afterSaleId = afterSale.getId();   // 注意：要确保 insert 之后 id 已经回填
            }
        }

        // 实在拿不到 id，就不要插日志，直接返回
        if (afterSaleId == null) {
            return;
        }

        AfterSaleLog log = new AfterSaleLog();
        log.setAfterSaleId(afterSaleId);
        log.setAction(afterSaleLogRecord.value());
        log.setOperator(afterSaleLogRecord.operator());
        log.setCreateTime(LocalDateTime.now());

        logMapper.insert(log);
    }
}
