package com.dangdang.param.parse;

import com.dangdang.common.functional.login.ILogin;
import com.dangdang.ddframework.core.VariableStore;
import com.dangdang.digital.meta.Media;
import com.dangdang.enumeration.VarKey;
import com.dangdang.readerV5.purchase.ListShoppingCart;
import com.dangdang.readerV5.reponse.Products;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cailianjie on 2015-7-14.
 * 获取购物车中商品id
 * #GetShopCartProductId#数量
 */
public class GetShopCartProductIdParse implements IParamParse{


    @Override
    public Object parse(Map<String, String> param) throws Exception {
        return null;
    }

    @Override
    public void parse(Map<String, String> paramMap, String key, String param) throws Exception {
        //获取购物车中的商品

        ILogin login=(ILogin) VariableStore.get(VarKey.LOGIN);
        ListShoppingCart listShoppingCart=null;
        if(login!=null) {
            listShoppingCart = new ListShoppingCart((ILogin) VariableStore.get(VarKey.LOGIN));
            listShoppingCart.doWork();
        }
        else{
            listShoppingCart = new ListShoppingCart(paramMap.get("cartId"));
            listShoppingCart.doWork();
        }
        List productIds = new ArrayList();

        int number=1;
        if(StringUtils.isNotBlank(param)){
            number=Integer.parseInt(param);
        }

       // for (Products products : listShoppingCart.getReponseResult().getData().getProducts()) {
        //Map<String,String> productMap=new HashMap<String, String>();
        //productMap.put("productId", listShoppingCart.getReponseResult().getData().getProducts().get(0).getMediaId());
        //productMap.put("saleId", listShoppingCart.getReponseResult().getData().getProducts().get(0).getSaleId());

        for(Products media:listShoppingCart.getReponseResult().getData().getProducts()){
            if(productIds.size()>=number){
                break;
            }
            productIds.add(media.getMediaId());
        }


        paramMap.put(key, org.apache.commons.lang3.StringUtils.join(productIds,","));
    }
}
