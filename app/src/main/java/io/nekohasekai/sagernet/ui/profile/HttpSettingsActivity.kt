package pkg.pkkstore.ui.profile

import pkg.pkkstore.fmt.http.HttpBean

class HttpSettingsActivity : StandardV2RaySettingsActivity() {

    override fun createEntity() = HttpBean()

}
