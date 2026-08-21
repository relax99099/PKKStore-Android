package io.nekohasekai.sagernet.proxy.shadowtls

import io.nekohasekai.sagernet.fmt.v2ray.buildSingBoxOutboundTLS
import io.nekohasekai.sagernet.SingBoxOptions

fun buildSingBoxOutboundShadowTLSBean(bean: ShadowTLSBean): SingBoxOptions.Outbound_ShadowTLSOptions {
    return SingBoxOptions.Outbound_ShadowTLSOptions().apply {
        type = "shadowtls"
        server = bean.serverAddress
        server_port = bean.serverPort
        version = bean.version
        password = bean.password
        tls = buildSingBoxOutboundTLS(bean)
    }
}
