package flexygames

import grails.gorm.transactions.Transactional
import org.geeks.browserdetection.UserAgentIdentService

@Transactional(readOnly = true)
class DisplayService {

    static transactional = false

    UserAgentIdentService userAgentIdentService

    def isMobileDevice(request) {
        Boolean isMobile = null
        try {
            if ('mobile'.equals(request.session.flavour)) {
                isMobile = true
            } else if ('desktop'.equals(request.session.flavour)) {
                isMobile = false
            } else {
                if (userAgentIdentService.isMobile()) {
                    isMobile = true
                } else {
                    isMobile = false
                }
            }
        } catch (IllegalStateException e) {
            // Session might not be available if response was already committed
            // In this case, default to desktop for admin endpoints
            isMobile = false
        }
        return isMobile
    }
}
