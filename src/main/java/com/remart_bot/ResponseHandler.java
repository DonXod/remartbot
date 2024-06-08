package com.remart_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Map;

import static com.remart_bot.Constants.ACCEPT_CONTACT_INPUT;
import static com.remart_bot.Constants.ACCEPT_CONTACT_OUTPUT;
import static com.remart_bot.Constants.ACCEPT_PROJECT_INPUT;
import static com.remart_bot.Constants.ACCEPT_PROJECT_OUTPUT;
import static com.remart_bot.Constants.MENU;
import static com.remart_bot.Constants.PORTFOLIO;
import static com.remart_bot.Constants.SM1_MENU_1;
import static com.remart_bot.Constants.SM1_MENU_2;
import static com.remart_bot.Constants.SM1_MENU_3;
import static com.remart_bot.Constants.SM1_MENU_4;
import static com.remart_bot.Constants.SM1_OPTION_1;
import static com.remart_bot.Constants.SM1_OPTION_2;
import static com.remart_bot.Constants.SM1_OPTION_3;
import static com.remart_bot.Constants.SM1_OPTION_4;
import static com.remart_bot.Constants.START_MENU_1;
import static com.remart_bot.Constants.START_MENU_2;
import static com.remart_bot.Constants.START_MENU_3;
import static com.remart_bot.Constants.START_MENU_OPTION_1;
import static com.remart_bot.Constants.START_MENU_OPTION_2;
import static com.remart_bot.Constants.START_MENU_OPTION_3;
import static com.remart_bot.Constants.START_TEXT;
import static com.remart_bot.Constants.STOP_TEXT;
import static com.remart_bot.Constants.TEAM;
import static com.remart_bot.Constants.UNEXPECTED_TEXT;
import static com.remart_bot.UserState.AWAITING_CONTACT;
import static com.remart_bot.UserState.AWAITING_MENU;
import static com.remart_bot.UserState.AWAITING_PROJECT;

public class ResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        sender.execute(message);
        message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_MENU_1);
        sender.execute(message);
        message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_MENU_2);
        sender.execute(message);
        promptWithKeyboardForState(chatId, START_MENU_3,
                KeyboardFactory.getStartMenuKeyboard(),
                UserState.AWAITING_START_MENU);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(STOP_TEXT);
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(UNEXPECTED_TEXT);
        sender.execute(sendMessage);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText() != null && message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
            return;
        }
        if (message.getText() != null && message.getText().equalsIgnoreCase("/menu")) {
            replyToMenu(chatId);
            return;
        }

        switch (chatStates.get(chatId)) {
            case AWAITING_MENU -> replyToMenu(chatId);
            case AWAITING_PROJECT -> replyAcceptProjectToMenu(chatId, message.getText());
            case AWAITING_CONTACT -> replyAcceptContactToMenu(chatId, message.getText());
            default -> unexpectedMessage(chatId);
        }
    }

    public void onUpdateReceived(Update update) {
        var callbackQuery = update.getCallbackQuery();
        deleteInlineButtons(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
        if (!callbackQuery.getData().equals(MENU)) {
            var message = new SendMessage();
            message.setChatId(callbackQuery.getMessage().getChatId());
            message.setText("Выбрано : " + callbackQuery.getData().charAt(callbackQuery.getData().length()-1));
            sender.execute(message);
        }
        switch (callbackQuery.getData()) {
            case START_MENU_OPTION_1 -> replyToSM1(callbackQuery.getMessage().getChatId());
            case START_MENU_OPTION_2 -> replyToSM2(callbackQuery.getMessage().getChatId());
            case START_MENU_OPTION_3 -> replyToSM3(callbackQuery.getMessage().getChatId());
            case MENU, SM1_OPTION_4 -> replyToMenu(callbackQuery.getMessage().getChatId());
            case SM1_OPTION_1 -> replyToSM1M1(callbackQuery.getMessage().getChatId());
            case SM1_OPTION_2 -> replyToSM1M2(callbackQuery.getMessage().getChatId());
            case SM1_OPTION_3 -> replyToSM1M3(callbackQuery.getMessage().getChatId());
            default -> unexpectedMessage(callbackQuery.getMessage().getChatId());
        }
    }

    private void promptWithKeyboardForState(long chatId, String text, ReplyKeyboard YesOrNo, UserState awaitingReorder) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(YesOrNo);
        sender.execute(sendMessage);
        chatStates.put(chatId, awaitingReorder);
    }

    private void replyToMenu(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_MENU_1);
        sender.execute(message);
        message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_MENU_2);
        sender.execute(message);
        promptWithKeyboardForState(chatId, START_MENU_3,
                KeyboardFactory.getStartMenuKeyboard(),
                UserState.AWAITING_START_MENU);
    }

    private void replyAcceptProjectToMenu(long chatId, String text) {
        promptWithKeyboardForState(chatId, ACCEPT_PROJECT_OUTPUT,
                KeyboardFactory.getMenuKeyboard(),
                UserState.AWAITING_MENU);
    }
    private void replyAcceptContactToMenu(long chatId, String text) {
        promptWithKeyboardForState(chatId, ACCEPT_CONTACT_OUTPUT,
                KeyboardFactory.getMenuKeyboard(),
                UserState.AWAITING_MENU);
    }

    private void replyToSM1(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(SM1_MENU_1);
        sender.execute(message);
        message = new SendMessage();
        message.setChatId(chatId);
        message.setText(SM1_MENU_2);
        sender.execute(message);
        message = new SendMessage();
        message.setChatId(chatId);
        message.setText(SM1_MENU_3);
        sender.execute(message);
        promptWithKeyboardForState(chatId, SM1_MENU_4,
                KeyboardFactory.getSM1Keyboard(),
                UserState.AWAITING_SM_1);
    }

    private void replyToSM2(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ACCEPT_PROJECT_INPUT);
        sender.execute(message);
        chatStates.put(chatId, AWAITING_PROJECT);
    }

    private void replyToSM3(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ACCEPT_CONTACT_INPUT);
        sender.execute(message);
        chatStates.put(chatId, AWAITING_CONTACT);
    }

    private void replyToSM1M1(long chatId) {
        promptWithKeyboardForState(chatId, PORTFOLIO,
                KeyboardFactory.getMenuKeyboard(),
                AWAITING_MENU);
    }

    private void replyToSM1M2(long chatId) {
        promptWithKeyboardForState(chatId, TEAM,
                KeyboardFactory.getMenuKeyboard(),
                AWAITING_MENU);
    }

    private void replyToSM1M3(long chatId) {
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ACCEPT_CONTACT_INPUT);
        sender.execute(message);
        chatStates.put(chatId, AWAITING_CONTACT);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }

    public void deleteInlineButtons(Long chatId, Integer messageId) {
        var message = new EditMessageReplyMarkup();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        sender.execute(message);
    }
}
