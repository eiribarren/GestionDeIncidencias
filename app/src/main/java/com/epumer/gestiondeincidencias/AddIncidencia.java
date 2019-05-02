package com.epumer.gestiondeincidencias;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddIncidenciaListener} interface
 * to handle interaction events.
 * Use the {@link AddIncidencia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddIncidencia extends Fragment {
    ImageView imagen;
    EditText descripcion;
    Button obrir;
    String urlImagen;

    private AddIncidenciaListener mListener;

    public AddIncidencia() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddIncidencia newInstance() {
        AddIncidencia fragment = new AddIncidencia();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_incidencia, container, false);

        imagen = v.findViewById(R.id.imagenIncidencia);
        descripcion = v.findViewById(R.id.descripcionIncidencia);
        obrir = v.findViewById(R.id.obrir);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.putImage();
            }
        });
        obrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Incidencia incidencia = new Incidencia();

                incidencia.setDescripcion(descripcion.getText().toString());
                incidencia.setResuelta(false);
                incidencia.setUrlImagen(urlImagen);

                mListener.saveIncidencia(incidencia);
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddIncidenciaListener) {
            mListener = (AddIncidenciaListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddIncidenciaListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void putImage(Bitmap bitmap, String urlImagen) {
        imagen.setImageBitmap(bitmap);
        this.urlImagen = urlImagen;
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
    public interface AddIncidenciaListener {
        void saveIncidencia(Incidencia incidencia);
        void putImage();
    }
}
