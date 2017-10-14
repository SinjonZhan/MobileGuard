package com.soleil.mobileguard;

import android.test.AndroidTestCase;

import org.junit.Test;


public class BlackDaoTest extends AndroidTestCase{
    @Test
    public void testAdd() throws Exception {

//        System.out.println("------------------------------"+  getContext().getFilesDir());
//        System.out.println("------------------------------"+  getContext().getCacheDir());
  System.out.println("------------------------------"+  getContext().getResources().getDisplayMetrics().density);
  System.out.println("------------------------------"+  getContext().getResources().getDisplayMetrics().densityDpi);





   /*     LockDao lockDao = new LockDao(getContext());
        lockDao.add("aa");
        lockDao.add("bb");
        Log.d("lockDao", lockDao.getAllLockedDatas() + "");
        lockDao.remove("aa");
        Log.d("lockDao", lockDao.getAllLockedDatas() + "");*/


/*        System.out.println("-----------"+ PhoneLocationEngine.mobileQuery("15626257844", getContext()));
        BlackDao bd = new BlackDao(getContext());


        for (int i = 1; i < ; i++) {

        bd.add("12345" + i, BlackTable.TEL);

        }
        List<BlackBean> datas = bd.getAllDatas();
        System.out.println(datas+"---------------------------");

        bd.update(datas.get(0).getPhone(), BlackTable.ALL);
        datas = bd.getAllDatas();

        System.out.println(datas+"---------------------------");*/


    }

}