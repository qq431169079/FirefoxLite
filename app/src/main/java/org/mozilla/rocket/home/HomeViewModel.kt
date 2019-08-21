package org.mozilla.rocket.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.Settings
import org.mozilla.rocket.download.SingleLiveEvent
import org.mozilla.rocket.home.topsites.domain.GetTopSitesUseCase
import org.mozilla.rocket.home.topsites.domain.PinTopSiteUseCase
import org.mozilla.rocket.home.topsites.domain.RemoveTopSiteUseCase
import org.mozilla.rocket.home.topsites.domain.TopSitesConfigsUseCase
import org.mozilla.rocket.home.topsites.ui.Site
import org.mozilla.rocket.home.topsites.ui.SitePage

class HomeViewModel(
    private val settings: Settings,
    private val getTopSitesUseCase: GetTopSitesUseCase,
    topSitesConfigsUseCase: TopSitesConfigsUseCase,
    private val pinTopSiteUseCase: PinTopSiteUseCase,
    private val removeTopSiteUseCase: RemoveTopSiteUseCase
) : ViewModel() {

    val sitePages = MutableLiveData<List<SitePage>>()
    val pinEnabled = MutableLiveData<Boolean>().apply { value = topSitesConfigsUseCase().isPinEnabled }

    val toggleBackgroundColor = SingleLiveEvent<Unit>()
    val resetBackgroundColor = SingleLiveEvent<Unit>()
    val topSiteClicked = SingleLiveEvent<Site>()
    val topSiteLongClicked = SingleLiveEvent<Site>()

    private fun List<Site>.toSitePages(): List<SitePage> = chunked(TOP_SITES_PER_PAGE)
            .filterIndexed { index, _ -> index < TOP_SITES_MAX_PAGE_SIZE }
            .map { SitePage(it) }

    fun updateTopSitesData() {
        getTopSitesUseCase { sitePages.value = it.toSitePages() }
    }

    fun onBackgroundViewDoubleTap(): Boolean {
        // Not allowed double tap to switch theme when night mode is on
        if (settings.isNightModeEnable) return false

        toggleBackgroundColor.call()
        return true
    }

    fun onBackgroundViewLongPress() {
        // Not allowed long press to reset theme when night mode is on
        if (settings.isNightModeEnable) return

        resetBackgroundColor.call()
    }

    fun onShoppingButtonClicked() {
        // TODO:
    }

    fun onTopSiteClicked(site: Site, position: Int) {
        topSiteClicked.value = site
        val allowToLogTitle = when (site) {
            is Site.FixedSite -> true
            is Site.RemovableSite -> site.isDefault
        }
        val title = if (allowToLogTitle) site.title else ""
        TelemetryWrapper.clickTopSiteOn(position, title)
    }

    fun onTopSiteLongClicked(site: Site): Boolean =
            if (site is Site.RemovableSite) {
                topSiteLongClicked.value = site
                true
            } else {
                false
            }

    fun onPinTopSiteClicked(site: Site) {
        pinTopSiteUseCase(site)
        updateTopSitesData()
    }

    fun onRemoveTopSiteClicked(site: Site) {
        removeTopSiteUseCase(site) {
            updateTopSitesData()
        }
    }

    companion object {
        private const val TOP_SITES_MAX_PAGE_SIZE = 2
        private const val TOP_SITES_PER_PAGE = 8
    }
}