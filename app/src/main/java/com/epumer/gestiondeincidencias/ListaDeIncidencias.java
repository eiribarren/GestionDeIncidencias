package com.epumer.gestiondeincidencias;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListaDeIncidenciasListener} interface
 * to handle interaction events.
 * Use the {@link ListaDeIncidencias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaDeIncidencias extends Fragment {
    private ListaDeIncidenciasListener mListener;
    private ArrayList<Incidencia> incidencias;
    ListaDeIncidenciasAdapter ldtadapter;

    public ListaDeIncidencias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListaDeIncidencias.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaDeIncidencias newInstance() {
        ListaDeIncidencias fragment = new ListaDeIncidencias();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incidencias = new ArrayList<Incidencia>();
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_de_incidencias, container, false);

        RecyclerView rv = v.findViewById(R.id.listaTareasRecycler);

        ldtadapter = new ListaDeIncidenciasAdapter(incidencias);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                llm.getOrientation());

        rv.addItemDecoration(dividerItemDecoration);

        rv.setAdapter(ldtadapter);
        rv.setLayoutManager(llm);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListaDeIncidenciasListener) {
            mListener = (ListaDeIncidenciasListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListaDeIncidenciasListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addIncidencia(Incidencia incidencia) {
        incidencias.add(incidencia);
        ldtadapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ListaDeIncidenciasListener {
        void ponerImagen(ImageView imagen, String url);
        void resolverIncidencia(Incidencia incidencia, boolean isChecked);
    }

    public class ListaDeIncidenciasAdapter extends RecyclerView.Adapter<ListaDeIncidenciasAdapter.IncidenciaViewHolder> {

        ArrayList<Incidencia> incidencias;

        public ListaDeIncidenciasAdapter(ArrayList<Incidencia> incidencias) {
            this.incidencias = incidencias;
        }

        @NonNull
        @Override
        public IncidenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.incidencia_holder, parent, false);

            IncidenciaViewHolder tvh = new IncidenciaViewHolder(v);

            return tvh;
        }

        @Override
        public void onBindViewHolder(@NonNull final IncidenciaViewHolder incidenciaViewHolder, int i) {
            incidenciaViewHolder.setIncidencia(incidencias.get(i));
            incidenciaViewHolder.descripcion.setText(incidencias.get(i).getDescripcion());
            incidenciaViewHolder.aula.setText(incidencias.get(i).getAula());
            incidenciaViewHolder.resuelta.setChecked(incidencias.get(i).isResuelta());
            mListener.ponerImagen(incidenciaViewHolder.imagen, incidencias.get(i).getUrlImagen());
        }

        @Override
        public int getItemCount() {
            return incidencias.size();
        }

        public class IncidenciaViewHolder extends RecyclerView.ViewHolder {

            ImageView imagen, clearImage;
            TextView descripcion, aula;
            CheckBox resuelta;

            public void setIncidencia(Incidencia incidencia) {
                this.incidencia = incidencia;
            }

            Incidencia incidencia;

            public IncidenciaViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagen);
                descripcion = itemView.findViewById(R.id.descripcion);
                aula = itemView.findViewById(R.id.aula);
                resuelta = itemView.findViewById(R.id.resuelto);
                clearImage = itemView.findViewById(R.id.clearImage);
                resuelta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mListener.resolverIncidencia(incidencia, isChecked);
                        if (isChecked) {
                            clearImage.animate().alpha(1).setDuration(1000).start();
                        } else {
                            clearImage.animate().alpha(0).setDuration(1000).start();
                        }
                    }
                });
            }
        }
    }
}
