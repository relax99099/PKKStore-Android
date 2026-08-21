package pkg.pkkstore.bg.proto

import pkg.pkkstore.database.DataStore
import pkg.pkkstore.database.ProxyEntity

class UrlTest {

    val link = DataStore.connectionTestURL
    private val timeout = 5000

    suspend fun doTest(profile: ProxyEntity): Int {
        return TestInstance(profile, link, timeout).doTest()
    }

}