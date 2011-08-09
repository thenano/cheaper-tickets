package mn.david.cheapertickets

cheaperTickets {

    dateFormat = 'dd/MM/yyyy'

    engine {
        submarino {
            webservice {
                baseURL = "http://www.submarinoviagens.com.br/Passagens/UIService/Service.svc/"
                search = "SearchGroupedFlights"
                status = "GetSearchStatus"
            }
        }
    }
}