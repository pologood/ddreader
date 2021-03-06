package com.dangdang.readerV5.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dangdang.autotest.common.FixtureBase;
import com.dangdang.ddframework.dataverify.ListVerify;
import com.dangdang.ddframework.dataverify.ValueVerify;
import com.dangdang.ddframework.dataverify.ExpressionVerify;
import com.dangdang.ddframework.reponse.ReponseV2;
import com.dangdang.digital.meta.ChannelMonthlyStrategy;
import com.dangdang.db.authority.MediaMonthlyAuthorityDb;
import com.dangdang.db.comment.TagInfoDb;
import com.dangdang.db.digital.ChannelDb;
import com.dangdang.db.digital.ChannelMonthlyStrategyDb;
import com.dangdang.db.digital.ChannelSubUserDb;
import com.dangdang.db.ucenter.LoginRecordDb;
import com.dangdang.db.ucenter.UserDeviceDb;
import com.dangdang.readerV5.reponse.ChannelResponse;
import com.dangdang.readerV5.reponse.MonthlyStrategy;
import com.dangdang.readerV5.reponse.UserBaseInfo;

/**
 * 频道详情接口
 * @author guohaiying
 */
public class Channel extends FixtureBase{	
	ReponseV2<ChannelResponse> jsonResult;
	
    @Override
    public void doWork() throws Exception {
        super.doWork();
        jsonResult = JSONObject.parseObject(result.toString(),new TypeReference<ReponseV2<ChannelResponse >>(){});
    }

    @Override
    protected void dataVerify() throws Exception {
        if(reponseV2Base.getStatus().getCode()==0){
        	//验证title    tagNames  isMine
        	String paramChannelId = paramMap.get("cId");
        	String token = paramMap.get("token");
        	String custId;
        	com.dangdang.digital.meta.Channel expectedChannel = ChannelDb.getChannel(paramChannelId);
        	com.dangdang.readerV5.reponse.Channel actualChannel = null;
        	actualChannel = jsonResult.getData().getChannel();
        	String actualChannelId = actualChannel.getChannelId().toString();
        	
        	//验证icon title subNumber description IsAllowMonthly
        	dataVerifyManager.add(new ValueVerify<String>(actualChannel.getIcon(), expectedChannel.getIcon()).setVerifyContent("验证Icon"));
        	dataVerifyManager.add(new ValueVerify<String>(actualChannel.getTitle(), expectedChannel.getTitle()).setVerifyContent("验证Title"));
        	dataVerifyManager.add(new ExpressionVerify(Math.abs(actualChannel.getSubNumber()-expectedChannel.getSubNumber())<10?true:false).setVerifyContent("验证SubNumber"));
        	dataVerifyManager.add(new ValueVerify<String>(actualChannel.getDescription(), expectedChannel.getDescription()).setVerifyContent("验证Description"));	
        	dataVerifyManager.add(new ValueVerify<Integer>(actualChannel.getIsAllowMonthly(), expectedChannel.getIsAllowMonthly()).setVerifyContent("验证IsAllowMonthly"));
              
        	//验证文章数
        	dataVerifyManager.add(new ValueVerify<Integer>(actualChannel.getDigestNum(), expectedChannel.getDigestNum()).setVerifyContent("验证文章数"));
        	
        	//验证标签 tagNames       	
        	String actualTagNames = actualChannel.getTagNames();
        	List<String> expectedTagNames = TagInfoDb.getTagNames(actualChannelId);
        	List<String> actualTagNameList = new ArrayList<String>(); 
        	if(expectedTagNames!=null){        		
        		for(String s: actualTagNames.split(",")){
        			actualTagNameList.add(s);
        		}
        		dataVerifyManager.add(new ListVerify(actualTagNameList, expectedTagNames, false).setVerifyContent("验证tagNames"));
        	}else
        		dataVerifyManager.add(new ValueVerify<String>(actualTagNameList.get(0), "").setVerifyContent("验证tagNames"));
        	
        	String actualUserMonthly = actualChannel.getHasBoughtMonthly();
        	String actualUserSub = actualChannel.getIsSub();
        	String actualIsMine = actualChannel.getIsMine(); 
        	custId = UserDeviceDb.getCustIdByToken(token);
        	if(custId!=null){
        		custId = UserDeviceDb.getCustIdByToken(token);
        		       		
        		//验证用户是否有包月权限 hasBoughtMonthly
        		String expectedUserMonthly = MediaMonthlyAuthorityDb.getUserMonthlyAuthority(custId, actualChannelId);
        		dataVerifyManager.add(new ValueVerify<String>(actualUserMonthly, expectedUserMonthly).setVerifyContent("验证用户是否有包月权限"));
        	
        		//验证用户是否已订阅 isSub
        		String expectedSub = ChannelSubUserDb.isSub(custId, actualChannelId,1);
        		dataVerifyManager.add(new ValueVerify<String>(actualUserSub, expectedSub).setVerifyContent("验证用户是否已订阅"));
        	
        		//验证是否是自己的频道 isMine
        		String expectedChannelId = ChannelDb.getChannelIdByCustId(custId);
        		if(actualChannelId.equals(expectedChannelId))
        			dataVerifyManager.add(new ValueVerify<String>(actualIsMine, "1").setVerifyContent("验证是否是自己的频道"));
        		else
        			dataVerifyManager.add(new ValueVerify<String>(actualIsMine, "0").setVerifyContent("验证是否是自己的频道"));
        	}else{
        		dataVerifyManager.add(new ValueVerify<String>(actualUserMonthly, "0").setVerifyContent("验证用户是否有包月权限"));
        		dataVerifyManager.add(new ValueVerify<String>(actualUserSub, "0").setVerifyContent("验证用户是否已订阅"));
        		dataVerifyManager.add(new ValueVerify<String>(actualIsMine, "0").setVerifyContent("验证是否是自己的频道"));
        	}
       		//验证 nickName ChannelOwner
    		UserBaseInfo actualUser = actualChannel.getUserBaseInfo();
    		Map<String, Object> expectedUser = LoginRecordDb.getUserLoginRecord(String.valueOf(actualChannel.getChannelId()));
    		if(expectedUser==null||expectedUser.equals(null))
    			//有问题  可以去查login_record表
    			dataVerifyManager.add(new ValueVerify<String>(actualUser.getChannelOwner(), "0").setVerifyContent("验证ChannelOwner"));
    		else{
    			dataVerifyManager.add(new ValueVerify<String>(actualUser.getNickNameAll(), expectedUser.get("cust_nickname").toString()).setVerifyContent("验证nickName"));
    			dataVerifyManager.add(new ValueVerify<String>(actualUser.getChannelOwner(), String.valueOf(expectedUser.get("channel_owner").toString())).setVerifyContent("验证ChannelOwner"));
    		}
    		
    		//验证MonthlyStrategy
    		List<MonthlyStrategy> actualMonthlyStrategyList = actualChannel.getChannelMonthlyStrategy();
    		List<ChannelMonthlyStrategy> expectedMonthlyStrategyList = ChannelMonthlyStrategyDb.getChannelMonthlyStrategy(paramChannelId);
    		if(expectedChannel.getIsAllowMonthly()==1&&expectedMonthlyStrategyList!=null){
    			for(int i=0; i<actualMonthlyStrategyList.size(); i++){
    				dataVerifyManager.add(new ValueVerify<Long>(actualMonthlyStrategyList.get(i).getId(),expectedMonthlyStrategyList.get(i).getId()).setVerifyContent("验证Id"));
    				dataVerifyManager.add(new ValueVerify<Long>(actualMonthlyStrategyList.get(i).getAndroid(),expectedMonthlyStrategyList.get(i).getAndroid()).setVerifyContent("验证Android"));
    				dataVerifyManager.add(new ValueVerify<Long>(actualMonthlyStrategyList.get(i).getIos(),expectedMonthlyStrategyList.get(i).getIos()).setVerifyContent("验证Ios"));
    				dataVerifyManager.add(new ValueVerify<String>(actualMonthlyStrategyList.get(i).getName(),expectedMonthlyStrategyList.get(i).getName()).setVerifyContent("验证Name"));
    				dataVerifyManager.add(new ValueVerify<Long>(actualMonthlyStrategyList.get(i).getOriginalPrice(),expectedMonthlyStrategyList.get(i).getOriginalPrice()).setVerifyContent("验证OriginalPrice"));
    				dataVerifyManager.add(new ValueVerify<Integer>(actualMonthlyStrategyList.get(i).getPlatform(),expectedMonthlyStrategyList.get(i).getPlatform()).setVerifyContent("验证Platform"));
    				dataVerifyManager.add(new ValueVerify<Integer>(actualMonthlyStrategyList.get(i).getType(),expectedMonthlyStrategyList.get(i).getType()).setVerifyContent("验证Type"));
    			}
    		}
        }
        super.dataVerify();
    }
	
}
