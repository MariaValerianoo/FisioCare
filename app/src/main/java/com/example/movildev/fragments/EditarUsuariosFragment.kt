package com.example.movildev.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.movildev.R
import com.example.movildev.databinding.FragmentEditarUsuariosBinding
import com.example.movildev.model.Usuario
import com.example.movildev.viewmodels.UsuarioViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class EditarUsuariosFragment : Fragment() {



    private var _binding: FragmentEditarUsuariosBinding? = null
    private val binding get() = _binding!!



    private val viewModel: UsuarioViewModel by activityViewModels()



    private val args: EditarUsuariosFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarUsuariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarSpinner()
        configurarDatePicker()

        val usuarioId = args.usuarioId
        if (usuarioId != -1) {                           // modo edición
            viewModel.obtenerUsuarioPorId(usuarioId)
                .observe(viewLifecycleOwner) { u ->
                    u?.let { pintarUsuario(it) }
                }
        }

        binding.btnGuardar.setOnClickListener {
            try {
                val usuario = construirUsuario(usuarioId)

                if (usuarioId == -1)
                    viewModel.agregarUsuario(usuario)
                else
                    viewModel.actualizarUsuario(usuario)

                Toast.makeText(
                    requireContext(),
                    if (usuarioId == -1)
                        getString(R.string.usuario_creado)
                    else
                        getString(R.string.usuario_actualizado),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack()

            } catch (e: IllegalArgumentException) {
                Snackbar.make(
                    binding.root,
                    e.message ?: "Datos inválidos",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun configurarSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipo_documento_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )
            binding.spinnerDocumento.adapter = adapter
        }
    }

    private fun configurarDatePicker() {
        binding.inputFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    binding.inputFecha.setText(
                        "%02d/%02d/%04d".format(d, m + 1, y)
                    )
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun pintarUsuario(u: Usuario) = with(binding) {
        inputNombre.setText(u.nombre)
        spinnerDocumento.setSelection(
            (spinnerDocumento.adapter as ArrayAdapter<String>)
                .getPosition(u.tipoDocumento)
        )
        inputNumero.setText(u.documento)
        inputFecha.setText(u.fechaNacimiento)
        inputEmail.setText(u.email)

    }


    private fun construirUsuario(idAntiguo: Int): Usuario {
        val nombre          = binding.inputNombre.text.toString().trim()
        val tipoDocumento   = binding.spinnerDocumento.selectedItem as String
        val documento       = binding.inputNumero.text.toString().trim()
        val fechaNacimiento = binding.inputFecha.text.toString().trim()
        val email           = binding.inputEmail.text.toString().trim()


        val nombrePersona   = "Catalina Cañon"
        val telefono        = ""

        require(nombre.isNotEmpty())            { "El nombre es obligatorio" }
        require(documento.isNotEmpty())         { "El documento es obligatorio" }
        require(fechaNacimiento.isNotEmpty())   { "La fecha de nacimiento es obligatoria" }

        // Mayoría de edad (18) para usar “Cédula”
        if (tipoDocumento.equals("Cédula", ignoreCase = true) &&
            !esMayorDeEdad(fechaNacimiento)
        ) {
            throw IllegalArgumentException(
                "Para usar Cédula el usuario debe ser mayor de 18 años"
            )
        }

        val id = if (idAntiguo == -1) generateNewId() else idAntiguo

        return Usuario(
            id              = id,
            nombre          = nombre,
            tipoDocumento   = tipoDocumento,
            documento       = documento,
            fechaNacimiento = fechaNacimiento,
            email           = email,
            nombrePersona   = nombrePersona,
            telefono        = telefono
        )
    }


    private fun esMayorDeEdad(fecha: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val nacimiento = runCatching { sdf.parse(fecha) }.getOrNull() ?: return false

        val hoy = Calendar.getInstance()
        val cumple = Calendar.getInstance().apply { time = nacimiento }

        var edad = hoy.get(Calendar.YEAR) - cumple.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < cumple.get(Calendar.DAY_OF_YEAR)) edad--

        return edad >= 18
    }

    private fun generateNewId(): Int {
        val actual = viewModel.usuarios.value.orEmpty()
        return (actual.maxOfOrNull { it.id } ?: 0) + 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
