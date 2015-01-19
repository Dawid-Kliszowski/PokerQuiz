package pl.pokerquiz.pokerquiz.datamodel.gameCommunication.basicProtocol;

public enum MessageType {
    gamer_info,
    gamer_info_response,
    actual_game_state,
    notification,
    exchange_cards_request,
    Croupier_accept_response,
    answer_self_question_request,
    declare_question_as_correct_request,
    basic_move_response
}
