package com.example.movildev

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.movildev.adapters.PostAdapter
import com.example.movildev.databinding.ActivityMainBinding
import com.example.movildev.viewmodels.PostViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostAdapter
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("findme", "App started")
        adapter = PostAdapter()

        // Ajuste para pantallas con notch/punchhole
        ViewCompat.setOnApplyWindowInsetsListener(binding.header) { view0, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view0.setPadding(
                view0.paddingLeft,
                statusBarHeight,
                view0.paddingRight,
                view0.paddingBottom
            )
            insets
        }

        // Configuración del NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Navegación segura solo si estás dentro del grafo principal
        binding.bottomNav0.gotoInicio.setOnClickListener {
            if (navController.currentDestination?.id == R.id.inicioFragment) return@setOnClickListener
            if (isInMainGraph(navController)) {
                navController.navigate(R.id.inicioFragment)
            }
        }

        binding.bottomNav0.gotoCitas.setOnClickListener {
            if (isInMainGraph(navController)) {
                navController.navigate(R.id.landCitasFragment)
            }
        }

        binding.bottomNav0.gotoHistoria.setOnClickListener {
            if (isInMainGraph(navController)) {
                navController.navigate(R.id.historiaFragment)
            }
        }

        binding.bottomNav0.gotoPerfil.setOnClickListener {
            if (isInMainGraph(navController)) {
                navController.navigate(R.id.perfilFragment)
            }
        }

        // Cambiar el título e icono de toolbar dinámicamente
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val iconResId = arguments?.getInt("icon") ?: R.drawable.placeholder_icon
            binding.title.text = destination.label
            binding.toolbarIcon.setImageResource(iconResId)
        }

        // Botón de regreso
        binding.backBtn.setOnClickListener {
            navController.navigateUp()
        }
    }

    // Verifica si el fragmento actual pertenece al grafo principal (nav_main)
    private fun isInMainGraph(navController: NavController): Boolean {
        val currentId = navController.currentDestination?.id ?: return false
        val mainGraphDestinations = setOf(
            R.id.inicioFragment,
            R.id.usuariosFragment,
            R.id.perfilFragment,
            R.id.historiaFragment,
            R.id.landCitasFragment,
            R.id.facturaFragment,
            R.id.tratamientoFragment,
            R.id.telemedicinaFragment,
            R.id.postFragment
        )
        return currentId in mainGraphDestinations
    }
}
