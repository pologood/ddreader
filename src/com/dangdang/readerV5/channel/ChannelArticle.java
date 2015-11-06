package com.dangdang.readerV5.channel;

import com.dangdang.autotest.common.FixtureBase;
import com.dangdang.ddframework.reponse.ReponseV2;
import com.dangdang.readerV5.reponse.ChannelArticleReponse;

import fitnesse.slim.SystemUnderTest;

/**
 * 文章列表接口
 * @author guohaiying
 *
 */
public class ChannelArticle extends FixtureBase{
	
	ReponseV2<ChannelArticleReponse> reponseResult;
	
	@SystemUnderTest
	//public ChannelSQL sql = new ChannelSQL();
	public ChannelArticle(){
		addAction("channelArticle");
	}
	
    @Override
    protected void dataVerify() throws Exception {
        if(reponseV2Base.getStatus().getCode()==0){
        	//List<UrlList>  actual = ChannelBgImgDb.getBackImg(paramMap.get("type"));
            //dataVerifyManager.add(new ListVerify(jsonResult.getData().getUrlList(),actual, true));
            
        }
        super.dataVerify();
    }
}
