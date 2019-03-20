package com.practice.phuc.ums_husc;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.practice.phuc.ums_husc.Adapter.NewsRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainFragment extends Fragment {
    private Context context;
    private List<THONGBAO> thongBaoList;

    // Cast json to model
    Moshi moshi;
    Type usersType;
    JsonAdapter<List<THONGBAO>> jsonAdapter;

    RecyclerView rvItems;
    LinearLayout layoutThongBao;
    RelativeLayout layoutLoading;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(Context context) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.context = context;
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("UMS_HUSC", "On create MainFragment");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("UMS_HUSC", "On create VIEW MainFragment");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        rvItems = (RecyclerView) view.findViewById(R.id.rv_thongBao);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);

        layoutThongBao = view.findViewById(R.id.layout_thongBao);
        layoutLoading = view.findViewById(R.id.loading_progress_layout);

        if (this.getArguments() == null) {
            new LoadThongBaoTask().execute((String) null);
        } else {
            hienThiThongBao();
        }

        return view;
    }

    @Override
    public void onPause() {
        Log.d("UMS_HUSC", "On pause MainFragment");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCreated", true);
        this.setArguments(bundle);
        super.onPause();
    }

    public class LoadThongBaoTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            setThongBaos(loadThongBao());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                hienThiThongBao();
                showProgress(false);
            }
        }
    }

    // Lay danh sach thong bao tu may chu duoi dang chuoi JSON
    private String loadThongBao(){
        String result = json;
//        final OkHttpClient okHttpClient = new OkHttpClient();
//        // Tao request
//        Request request = new Request.Builder()
//                .url(Reference.HOST + Reference.LOAD_LY_LICH_API)
//                .get().build();
//
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response != null) {
//                if (response.code() == Reference.OK)
//                    result = response.body().string();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;
    }

    // Doi chuoi JSON sang model
    private void setThongBaos(String json) {
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(List.class, THONGBAO.class);
        jsonAdapter = moshi.adapter(usersType);

        try {
            thongBaoList = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hien thi danh sach thong bao len view
    private void hienThiThongBao() {
        rvItems.setAdapter(new NewsRecyclerDataAdapter(thongBaoList, MainFragment.this.context));
    }

    // Hien thi loading
    private void showProgress(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        layoutThongBao.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private String json = "[{\"LIENKETs\":[{\"MaLienKet\":1,\"MaThongBao\":1,\"DuongDan\":\"www.google.com\"},{\"MaLienKet\":2,\"MaThongBao\":1,\"DuongDan\":\"www.youtube.com\"}],\"MaThongBao\":1,\"TieuDe\":\"Thông báo v/v mở bổ sung các lớp học phần lần 9 học kỳ 2/2018-2019\",\"ThoiGianDang\":\"2019-03-15T08:05:00\",\"NoiDung\":\"Nhằm tạo điều kiện cho sinh viên khóa 39 trở về trước được học lại các học phần theo đúng kế hoạch đào tạo, căn cứ đề nghị của Khoa quản lý chuyên môn, Nhà trường thông báo cho các đơn vị và sinh viên biết việc mở lớp học phần bổ sung lần 9 trong học kỳ II, năm học 2018 – 2019. Chi tiết thông báo xem tại nội dung đính kèm sau:\"}," +
            "{\"LIENKETs\":[],\"MaThongBao\":2,\"TieuDe\":\"Thông báo v/v cho sinh viên nghỉ học \",\"ThoiGianDang\":\"2019-03-18T08:51:00\",\"NoiDung\":\"Thực hiện công văn số 60/ĐHKH-TCHC ngày 15/03/2019 của Hiệu trưởng về việc tổ chức các hoạt động trong hội trại 26/03; Phòng Đào tạo Đại học thông báo cho toàn thể sinh viên được nghỉ học ngày 23/03/2019 để tham gia chương trình \\\"Hội trại 26-03\\\". Giảng viên các lớp học phần chủ động liên hệ Phòng Đào tạo Đại học để đăng ký lịch dạy bù, đảm bảo theo kế hoạch năm học 2018-2019.\"}," +
            "{\"LIENKETs\":[],\"MaThongBao\":3,\"TieuDe\":\"Thông báo v/v nhận bằng tốt nghiệp đợt 1 năm 2019 \",\"ThoiGianDang\":\"2019-03-12T07:56:00\",\"NoiDung\":\"Phòng Đào tạo Đại học thông báo cho sinh viên được công nhận tốt nghiệp theo Quyết định số 49/QĐ-ĐHKH ngày 26 tháng 02 năm 2019 đến nhận bằng tại Phòng Đào tạo Đại học. Khi đến nhận bằng đề nghị mang theo giấy tờ tùy thân.\"}," +
            "{\"LIENKETs\":[{\"MaLienKet\":1,\"MaThongBao\":1,\"DuongDan\":\"www.google.com\"},{\"MaLienKet\":2,\"MaThongBao\":1,\"DuongDan\":\"www.youtube.com\"}],\"MaThongBao\":1,\"TieuDe\":\"Thông báo v/v mở bổ sung các lớp học phần lần 9 học kỳ 2/2018-2019\",\"ThoiGianDang\":\"2019-03-15T08:05:00\",\"NoiDung\":\"Nhằm tạo điều kiện cho sinh viên khóa 39 trở về trước được học lại các học phần theo đúng kế hoạch đào tạo, căn cứ đề nghị của Khoa quản lý chuyên môn, Nhà trường thông báo cho các đơn vị và sinh viên biết việc mở lớp học phần bổ sung lần 9 trong học kỳ II, năm học 2018 – 2019. Chi tiết thông báo xem tại nội dung đính kèm sau:\"}," +
            "{\"LIENKETs\":[{\"MaLienKet\":1,\"MaThongBao\":1,\"DuongDan\":\"www.google.com\"},{\"MaLienKet\":2,\"MaThongBao\":1,\"DuongDan\":\"www.youtube.com\"}],\"MaThongBao\":1,\"TieuDe\":\"Thông báo v/v mở bổ sung các lớp học phần lần 9 học kỳ 2/2018-2019\",\"ThoiGianDang\":\"2019-03-15T08:05:00\",\"NoiDung\":\"Nhằm tạo điều kiện cho sinh viên khóa 39 trở về trước được học lại các học phần theo đúng kế hoạch đào tạo, căn cứ đề nghị của Khoa quản lý chuyên môn, Nhà trường thông báo cho các đơn vị và sinh viên biết việc mở lớp học phần bổ sung lần 9 trong học kỳ II, năm học 2018 – 2019. Chi tiết thông báo xem tại nội dung đính kèm sau:\"}," +
            "{\"LIENKETs\":[{\"MaLienKet\":1,\"MaThongBao\":1,\"DuongDan\":\"www.google.com\"},{\"MaLienKet\":2,\"MaThongBao\":1,\"DuongDan\":\"www.youtube.com\"}],\"MaThongBao\":1,\"TieuDe\":\"Thông báo v/v mở bổ sung các lớp học phần lần 9 học kỳ 2/2018-2019\",\"ThoiGianDang\":\"2019-03-15T08:05:00\",\"NoiDung\":\"Nhằm tạo điều kiện cho sinh viên khóa 39 trở về trước được học lại các học phần theo đúng kế hoạch đào tạo, căn cứ đề nghị của Khoa quản lý chuyên môn, Nhà trường thông báo cho các đơn vị và sinh viên biết việc mở lớp học phần bổ sung lần 9 trong học kỳ II, năm học 2018 – 2019. Chi tiết thông báo xem tại nội dung đính kèm sau:\"}]";
}
