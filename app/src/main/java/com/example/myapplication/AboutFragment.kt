package com.example.myapplication
//Фрагмент "О компании"
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class AboutFragment : Fragment() {

    private lateinit var btnAbout: Button
    private lateinit var btnContacts: Button
    private lateinit var btnAdvertising: Button

    private lateinit var aboutCompanyFragment: AboutCompanySmallFragment
    private lateinit var collectiveFragment: CollectiveFragment
    private lateinit var advertisingFragment: AdvertisingFragment
    private var activeFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        btnAbout = view.findViewById(R.id.btn_about)
        btnContacts = view.findViewById(R.id.btn_contacts)
        btnAdvertising = view.findViewById(R.id.btn_advertising)

        aboutCompanyFragment = AboutCompanySmallFragment()
        collectiveFragment = CollectiveFragment()
        advertisingFragment = AdvertisingFragment()

        val childFragmentManager = childFragmentManager
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_about_container, advertisingFragment, "3").hide(advertisingFragment)
            .add(R.id.fragment_about_container, collectiveFragment, "2").hide(collectiveFragment)
            .add(R.id.fragment_about_container, aboutCompanyFragment, "1")
            .commit()

        activeFragment = aboutCompanyFragment
        highlightButton(btnAbout)

        btnAbout.setOnClickListener {
            switchInnerFragment(aboutCompanyFragment)
            highlightButton(btnAbout)
        }

        btnContacts.setOnClickListener {
            switchInnerFragment(collectiveFragment)
            highlightButton(btnContacts)
        }

        btnAdvertising.setOnClickListener {
            switchInnerFragment(advertisingFragment)
            highlightButton(btnAdvertising)
        }

        return view
    }

    private fun switchInnerFragment(targetFragment: Fragment) {
        if (targetFragment != activeFragment) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.hide(activeFragment!!)
            transaction.show(targetFragment)
            transaction.commit()
            activeFragment = targetFragment
        }
    }

    private fun highlightButton(selectedButton: Button) {
        resetButtonStyle(btnAbout)
        resetButtonStyle(btnContacts)
        resetButtonStyle(btnAdvertising)

        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.VGTRKdop))
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun resetButtonStyle(button: Button) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.VGTRKmain))
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }
}

