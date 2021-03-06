package mn.david.cheapertickets.configuration

import spock.lang.Specification
import groovy.mock.interceptor.MockFor
import groovy.util.logging.Commons
import mn.david.cheapertickets.configuration.Configuration

/**
 * User: David Nelson <http://github.com/dmnelson>
 * Date: 8/6/11
 * Time: 10:45 PM
 */
class ConfigurationSpec extends Specification {

    def "Retrieving a configuration"() {

        given: "A Configuration class that returns an ordinary mock"
        def mock = mockForConfigObject();
        def configurationInstance = new Configuration();
        configurationInstance.config = mock.proxyInstance();

        when: "A configuration entry is acessed"
        def configuration = configurationInstance.config;
        def aConfig = configuration.cheaperTickets.a.config;

        then: "The value should be a ConfigObject and not null"
        aConfig != null;
        aConfig instanceof ConfigObject;
        mock.verify(configuration);

    }


    def "Loading all configuration from a file"() {

        given: "A temp file"
        File configTempFile = writeConfigFile("""
                                        my {
                                            test {
                                                config = 'WooHoo'
                                            }
                                        }
                                    """);

        and: "A mocked 'getConfigScript' method that returns that file"
        Configuration configuration = new Configuration();
        configuration.build(configTempFile.toURL())

        when: "The deepest configuration is acessed"
        def deepestConfig = configuration.config.my.test.config;

        then: "The value of the configuration should be the same as defined on the file"
        deepestConfig == 'WooHoo';

        when: "One of the intermediate level configuration is acessed"
        def anotherConfig = configuration.config.my.test

        then: "A wrapper object should be returned"
        anotherConfig instanceof ConfigObject

        when: "Using the configuration on the standard (static) way"
        def config = Configuration.get { it };
        def deepestFromDefault = Configuration.get { my.test.config }

        then: "It should be the same as the manual way"
        config == configuration.config;
        deepestConfig == deepestFromDefault;

        cleanup: "Deleting the file and reseting the Configuration metaclass"
        assert configTempFile.delete();

    }

    def "Loading from actual config file"() {

        given:
        def configuration = new Configuration();
        def configFile = configuration.loadDefaultConfigScript();

        expect:
        configFile != null;

        when:
        configuration.build(configFile)
        def config = configuration.config;
        def configMap = config?.flatten();

        then:
        config != null;
        config instanceof ConfigObject;
        !configMap.isEmpty()

        when:
        configuration.build();

        then:
        config == configuration.config;

    }

    private static MockFor mockForConfigObject() {
        new MockFor(ConfigObject, true).with { it ->
            demand.containsKey(0..1) { key -> true }
            demand.get(0..1) { name -> it.proxyInstance(); }
            return it;
        };
    }

    private static writeConfigFile(def contents) {
        File classLoaderRoot = new File(ClassLoader.getSystemClassLoader().getResource("").toURI());
        File configFile = new File(classLoaderRoot, 'cheapertickets_config.groovy');
        configFile.withWriter { w ->
            w << contents
        }
        return configFile;
    }

    def setup() {
        Configuration.configurationInstance = null;
    }

    def cleanup() {
        Configuration.configurationInstance = null;
    }
}