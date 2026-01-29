package com.sxps.xsps.service;

import com.sxps.xsps.pojo.AfterSaleLog;
import java.util.List;

public interface MerchantAfterSaleLogService {
    List<AfterSaleLog> listByAfterSaleId(Long afterSaleId);
}
