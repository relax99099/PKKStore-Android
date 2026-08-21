package pkg.pkkstore.bg.proto

import pkg.pkkstore.BuildConfig
import pkg.pkkstore.bg.GuardedProcessPool
import pkg.pkkstore.database.ProxyEntity
import pkg.pkkstore.fmt.buildConfig
import pkg.pkkstore.ktx.Logs
import pkg.pkkstore.ktx.runOnDefaultDispatcher
import pkg.pkkstore.ktx.tryResume
import pkg.pkkstore.ktx.tryResumeWithException
import kotlinx.coroutines.delay
import libcore.Libcore
import pkg.pkkstore.net.LocalResolverImpl
import kotlin.coroutines.suspendCoroutine

class TestInstance(profile: ProxyEntity, val link: String, private val timeout: Int) :
    BoxInstance(profile) {

    suspend fun doTest(): Int {
        return suspendCoroutine { c ->
            processes = GuardedProcessPool {
                Logs.w(it)
                c.tryResumeWithException(it)
            }
            runOnDefaultDispatcher {
                use {
                    try {
                        init()
                        launch()
                        if (processes.processCount > 0) {
                            // wait for plugin start
                            delay(500)
                        }
                        c.tryResume(Libcore.urlTest(box, link, timeout))
                    } catch (e: Exception) {
                        c.tryResumeWithException(e)
                    }
                }
            }
        }
    }

    override fun buildConfig() {
        config = buildConfig(profile, true)
    }

    override suspend fun loadConfig() {
        // don't call destroyAllJsi here
        if (BuildConfig.DEBUG) Logs.d(config.config)
        box = Libcore.newSingBoxInstance(config.config, LocalResolverImpl)
    }

}
