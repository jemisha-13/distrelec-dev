import { Ga4SearchEvent } from "@features/tracking/events/ga4/ga4-search-event";
import { Ga4SearchSuggestionEvent } from "@features/tracking/events/ga4/ga4-search-suggestion-event";
import { SearchEvent } from "@features/tracking/events/search-event";
import { SearchSuggestionEvent } from "@features/tracking/events/search-suggestion-event";
import { AnalyticsCustomerType } from "@features/tracking/model/event-user-details";

export const MOCK_SEARCH_SUGGEST_EVENT: Ga4SearchSuggestionEvent = {
    event: 'suggestedSearch',
    search_term: 'tester',
    search_category: '',
    user: {
        logged_in: false,
        language: 'english',
        customer_type: AnalyticsCustomerType.B2C,
        mg: false,
    },
    page: { 
        document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
        url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
        category: 'pdp page', 
        market: 'CH' 
    },
}

export const MOCK_SEARCH_SUGGEST_DATA: SearchSuggestionEvent = {
    search_term: 'tester',
    search_category: ''
}

export const MOCK_SEARCH_EVENT: Ga4SearchEvent = {
    event: 'search',
    search_term: 'bulb',
    search_category: 'cat-L2D_379658',
    user: {
        logged_in: false,
        language: 'english',
        customer_type: AnalyticsCustomerType.B2C,
        mg: false,
    },
    page: { 
        document_title: 'RND 135-00207 | RND THT LED White 3000K 1.3cd 3mm Cylindrical | Distrelec Switzerland', 
        url: 'https://distrelec-ch.local:4200/en/tht-led-white-3000k-3cd-3mm-cylindrical-rnd-rnd-135-00207/p/30158802?itemList=cart', 
        category: 'pdp page', 
        market: 'CH' 
    },
}

export const MOCK_SEARCH_DATA: SearchEvent = {
    search_term: 'bulb',
    search_category: 'cat-L2D_379658'
}