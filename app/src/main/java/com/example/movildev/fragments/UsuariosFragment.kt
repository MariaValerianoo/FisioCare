package com.example.movildev.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels   // ← ViewModel compartido con la Activity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movildev.adapters.UsuarioItemAdapter
import com.example.movildev.databinding.FragmentUsuariosLayoutBinding
import com.example.movildev.model.Usuario
import com.example.movildev.viewmodels.UsuarioViewModel

class UsuariosFragment : Fragment() {

    /* ---------- view binding ---------- */

    private var _binding: FragmentUsuariosLayoutBinding? = null
    private val binding get() = _binding!!

    /* ---------- ViewModel compartido ---------- */

    private val viewModel: UsuarioViewModel by activityViewModels()

    /* ---------- RecyclerView adapter ---------- */

    private lateinit var adapter: UsuarioItemAdapter

    /* ---------- lifecycle ---------- */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsuariosLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* ── Adapter con callbacks ───────────────────────────── */

        adapter = UsuarioItemAdapter(
            onEditarClick = { usuario: Usuario ->
                val action = UsuariosFragmentDirections
                    .actionUsuariosFragmentToFormularioUsuarioFragment(usuario.id)
                findNavController().navigate(action)
            },
            onEliminarClick = { usuario: Usuario ->
                viewModel.eliminarUsuario(usuario)
            },
            onVerClick = { usuario: Usuario ->
                val action = UsuariosFragmentDirections
                    .actionUsuariosFragmentToDetalleUsuarioFragment(usuario.id)
                findNavController().navigate(action)
            }
        )

        /* ── RecyclerView ────────────────────────────────────── */

        binding.rvUsuarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@UsuariosFragment.adapter
        }

        /* ── Observa la lista de usuarios ────────────────────── */

        viewModel.usuarios.observe(viewLifecycleOwner) { lista ->
            adapter.data = lista.toList()
            adapter.notifyDataSetChanged()        // Si tu adapter es ListAdapter, elimínalo
        }

        /* ── Filtrado en tiempo real ─────────────────────────── */

        binding.inputBuscar.doOnTextChanged { text, _, _, _ ->
            val original = viewModel.usuarios.value.orEmpty()
            adapter.data = if (text.isNullOrBlank()) {
                original.toList()
            } else {
                original.filter { it.nombre.contains(text, ignoreCase = true) }
            }
            adapter.notifyDataSetChanged()
        }

        /* ── FAB: crear nuevo usuario ────────────────────────── */

        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(
                UsuariosFragmentDirections
                    .actionUsuariosFragmentToFormularioUsuarioFragment(-1)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
