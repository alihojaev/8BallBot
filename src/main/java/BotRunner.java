import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class BotRunner {

    static {
        ApiContextInitializer.init();
    }

    public static void main(String[] args) throws TelegramApiRequestException {
        final TelegramBotImpl bot = new TelegramBotImpl();
        new TelegramBotsApi()
                .registerBot(bot);
    }

    private static class TelegramBotImpl extends TelegramLongPollingBot {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        private final Map<Integer, List<String>> content;

        public TelegramBotImpl() {
            this.content = this.getCases();
        }

        @Override
        public void onUpdateReceived(final Update update) {
            if (update != null) {
                update.getUpdateId();
                SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());

                String text = update.getMessage().getText();
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                String sandedMessage = getMessage(text);

                System.out.println("\033[33m chatID: [" + update.getMessage().getChatId() + "] Полученное сообщение: [" + text + "] Отправленное сообщение: [" + sandedMessage + "] \033[33m");
                try {
                    sendMessage.setText(sandedMessage);
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage.enableMarkdown(true);
                    this.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getBotUsername() {
            return "EightBallByAliBot";
        }

        public String getBotToken() {
            return "1084533433:AAHm5sHOf1rsWKCI1JG-M945u8IYE-52728";
        }

        private String getMessage(String msg) {
            ArrayList<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow = new KeyboardRow();

            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            if (msg.equals("Встряхнуть \uD83C\uDFB1") || msg.equals("/start")) {
                keyboard.clear();
                keyboardRow.clear();
                keyboardRow.add("Встряхнуть \uD83C\uDFB1");
                keyboard.add(keyboardRow);
                replyKeyboardMarkup.setKeyboard(keyboard);

                return this.getRandomMessage();
            } else if (msg.equals("Шишки") || msg.equals("шишки")) {
                return "Наше все";
            } else if (msg.equals("Админ - петух") || msg.equals("админ - петух") || msg.equals("админ петух") || msg.equals("Админ петух")) {
                return "Сам петух \uD83D\uDC13";
            } else {
                return "Я не понимаю";
            }
        }

        private String getRandomMessage() {

            final Random random = new Random();
            return content.get(random.nextInt(3))
                    .get(random.nextInt(4));
        }

        private Map<Integer, List<String>> getCases() {
            final Map<Integer, List<String>> data = new HashMap<Integer, List<String>>();
            final List<String> firstCases = new ArrayList<String>();
            firstCases.add("Бесспорно");
            firstCases.add("Предрешено");
            firstCases.add("Никаких сомнений");
            firstCases.add("Определенно да");
            firstCases.add("Можешь быть уверен в этом");
            data.put(0, firstCases);
            final List<String> secondCases = new ArrayList<String>();
            secondCases.add("Мне кажется — «да»");
            secondCases.add("Вероятнее всего");
            secondCases.add("Хорошие перспективы");
            secondCases.add("Знаки говорят — «да»");
            secondCases.add("Да");
            data.put(1, secondCases);
            final List<String> thirdCases = new ArrayList<String>();
            thirdCases.add("Пока не ясно, попробуй снова");
            thirdCases.add("Спроси позже");
            thirdCases.add("Лучше не рассказывать");
            thirdCases.add("Сейчас нельзя предсказать");
            thirdCases.add("Сконцентрируйся и спроси опять");
            data.put(2, thirdCases);
            final List<String> fourthCases = new ArrayList<String>();
            fourthCases.add("Даже не думай");
            fourthCases.add("Мой ответ — «нет»");
            fourthCases.add("По моим данным — «нет»");
            fourthCases.add("Перспективы не очень хорошие");
            fourthCases.add("Весьма сомнительно");
            data.put(3, fourthCases);
            return data;
        }
    }
}
