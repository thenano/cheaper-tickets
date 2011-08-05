package mn.david.cheapertickets

import org.junit.Test

/**
 * User: David Nelson <http://github.com/dmnelson>
 * Date: 7/30/11
 * Time: 11:04 PM
 */
class FinderTest extends GroovyTestCase{

    @Test
    void testShouldSearchTickets(){
       new Finder().airfares {
           to BHZ from POA at '12/08/2011'
       }
    }
}