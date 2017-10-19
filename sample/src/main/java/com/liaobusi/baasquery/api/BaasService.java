package com.liaobusi.baasquery.api;

import com.liaobushi.query.BaasCall;
import com.liaobushi.query.Query;
import com.liaobushi.query.Service;
import com.liaobusi.baasquery.bean.SmileyPackageBean;

import java.util.List;

/**
 * Created by liaozhongjun on 2017/10/11.
 */
@Service
public interface BaasService {
    @Query(table = "dt_smiley_package", condition = "title=='小'")
    List<SmileyPackageBean> listSmileyPackages(String title, int type, int num, int limit);

    @Query(table = "dt_smiley_package", condition = "title CONTAINS '小'")
    BaasCall<List<SmileyPackageBean>> listSmileyPackages2(String title, int type, int num, int limit);
}
