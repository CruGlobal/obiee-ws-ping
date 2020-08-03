package org.cru.obieewsping;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.ws.BindingProvider;
import oracle.bi.web.soap.ReportParams;
import oracle.bi.web.soap.ReportRef;
import oracle.bi.web.soap.SAWSessionService;
import oracle.bi.web.soap.SAWSessionServiceSoap;
import oracle.bi.web.soap.XMLQueryExecutionOptions;
import oracle.bi.web.soap.XMLQueryOutputFormat;
import oracle.bi.web.soap.XmlViewService;
import oracle.bi.web.soap.XmlViewServiceSoap;
import org.ccci.obiee.client.init.AnswersServiceFactory;
import org.ccci.obiee.client.rowmap.impl.PortConfigurer;

@ApplicationScoped
public class ObieeWsTransactionService {

    private static final String VALIDATION_REPORT_PATH = "/shared/CCCi/SSW/Rowmap Session Validation Query";
    private static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(5);
    private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

    private final SAWSessionService sawSessionService;
    private final XmlViewService xmlViewService;


    private static final String ENDPOINT_BASE_URL;

    static {
        final String endpoint = System.getenv("ENDPOINT_BASE_URL");
        ENDPOINT_BASE_URL = endpoint != null ? endpoint : "https://insight-sso.cru.org";
    }

    public ObieeWsTransactionService() {
        AnswersServiceFactory factory = new AnswersServiceFactory();
        this.sawSessionService = factory.buildService(SAWSessionService.class);
        this.xmlViewService = factory.buildService(XmlViewService.class);
    }

    public void performSyntheticTransaction(String username, String password) {
        final SAWSessionServiceSoap sessionPort = sawSessionService.getSAWSessionServiceSoap();
        configurePort(sessionPort);

        final XmlViewServiceSoap xmlViewPort = xmlViewService.getXmlViewServiceSoap();
        configurePort(xmlViewPort);

        final String sessionId = sessionPort.logon(username, password);
        validateBiServerSession(sessionId, xmlViewPort);
        sessionPort.logoff(sessionId);
    }

    private void configurePort(Object port) {
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        setEndpointAddressIfNecessary(bindingProvider);

        PortConfigurer portConfigurer = new PortConfigurer(bindingProvider);
        portConfigurer.setDefaults();
        portConfigurer.setTimeouts(CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    private void setEndpointAddressIfNecessary(BindingProvider bindingProvider) throws AssertionError {
        String defaultEndpointAddress = (String) bindingProvider.getRequestContext()
            .get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);

        if (!defaultEndpointAddress.startsWith(ENDPOINT_BASE_URL)) {
            String newEndpointAddress = buildNewEndpointAddress(defaultEndpointAddress);
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, newEndpointAddress);
        }
    }

    private String buildNewEndpointAddress(String defaultEndpointAddress) throws AssertionError {
        Matcher matcher = Pattern.compile("^https?://[^/]*/(.*)").matcher(defaultEndpointAddress);
        boolean matches = matcher.matches();
        if (!matches) {
            throw new AssertionError("Can't find endpoint url suffix in " + defaultEndpointAddress);
        }
        String endpointUrlSuffix = matcher.group(1);
        return ENDPOINT_BASE_URL + "/" + endpointUrlSuffix;
    }

    private void validateBiServerSession(String sessionId, XmlViewServiceSoap xmlViewPort) {
        ReportRef report = new ReportRef();
        report.setReportPath(VALIDATION_REPORT_PATH);

        XMLQueryOutputFormat outputFormat = XMLQueryOutputFormat.SAW_ROWSET_SCHEMA_AND_DATA;
        XMLQueryExecutionOptions executionOptions = new XMLQueryExecutionOptions();
        executionOptions.setMaxRowsPerPage(1);
        executionOptions.setPresentationInfo(true);
        ReportParams reportParams = new ReportParams();
        xmlViewPort.executeXMLQuery(
            report,
            outputFormat,
            executionOptions,
            reportParams,
            sessionId
        );
    }
}
