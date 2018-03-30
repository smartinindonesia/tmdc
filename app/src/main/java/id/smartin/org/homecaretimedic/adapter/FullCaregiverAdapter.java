package id.smartin.org.homecaretimedic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.smartin.org.homecaretimedic.R;
import id.smartin.org.homecaretimedic.model.Caregiver;

/**
 * Created by Hafid on 9/23/2017.
 */

public class FullCaregiverAdapter extends RecyclerView.Adapter<FullCaregiverAdapter.MyViewHolder>{
    public static String TAG = "[FullCaregiverAdapter]";

    private List<Caregiver> caregiverList;
    private Context context;


    public FullCaregiverAdapter(Context context, List<Caregiver> caregiverList){
        this.context = context;
        this.caregiverList = caregiverList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.perawat_item, parent, false);
        return new FullCaregiverAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Caregiver caregiver = caregiverList.get(position);
        holder.nama.setText(caregiver.getFrontName()+" "+caregiver.getMiddleName()+" "+caregiver.getLastName());
        holder.kategori.setText(caregiver.getRegisterNurseNumber());
        holder.agama.setText(caregiver.getAddress());
        holder.pendidikan.setText(caregiver.getEmail());
        holder.skill.setText(caregiver.getPhoneNumber());
        holder.btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.namaPerawat)
        TextView nama;
        @BindView(R.id.kategoriPerawat)
        TextView kategori;
        @BindView(R.id.agamaPerawat)
        TextView agama;
        @BindView(R.id.skillPerawat)
        TextView skill;
        @BindView(R.id.pendidikanPerawat)
        TextView pendidikan;
        @BindView(R.id.btnPesan)
        Button btnPesan;
        @BindView(R.id.btnDetail)
        Button btnDetail;
        @BindView(R.id.profPic)
        ImageView profPic;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}