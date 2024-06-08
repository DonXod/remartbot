package com.remart_bot;

import lombok.val;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static com.remart_bot.Constants.*;

public class KeyboardFactory {

    public static ReplyKeyboard getStartMenuKeyboard() {
        val button1 = new InlineKeyboardButton("1");
        button1.setCallbackData(START_MENU_OPTION_1);
        val button2 = new InlineKeyboardButton("2");
        button2.setCallbackData(START_MENU_OPTION_2);
        val button3 = new InlineKeyboardButton("3");
        button3.setCallbackData(START_MENU_OPTION_3);
        return new InlineKeyboardMarkup(List.of(List.of(button1,button2,button3)));
    }

    public static ReplyKeyboard getSM1Keyboard() {
        val button1 = new InlineKeyboardButton("1");
        button1.setCallbackData(SM1_OPTION_1);
        val button2 = new InlineKeyboardButton("2");
        button2.setCallbackData(SM1_OPTION_2);
        val button3 = new InlineKeyboardButton("3");
        button3.setCallbackData(SM1_OPTION_3);
        val button4 = new InlineKeyboardButton("4");
        button4.setCallbackData(SM1_OPTION_4);
        return new InlineKeyboardMarkup(List.of(List.of(button1, button2, button3, button4)));
    }

    public static ReplyKeyboard getMenuKeyboard() {
        val button = new InlineKeyboardButton(MENU);
        button.setCallbackData(MENU);
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }

}
