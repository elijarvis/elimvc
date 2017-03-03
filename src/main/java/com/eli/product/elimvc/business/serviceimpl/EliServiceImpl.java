package com.eli.product.elimvc.business.serviceimpl;

import com.eli.product.elimvc.annotation.Service;
import com.eli.product.elimvc.business.service.EliService;

/**
 * @author eli
 * @description 服务层实现
 */
@Service("eliService")
public class EliServiceImpl implements EliService {
    @Override
    public String test(String param) {
        return "------EliServiceImpl----param:"+param+"------";
    }
}
