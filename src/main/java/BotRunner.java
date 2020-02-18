import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.util.*;


public class BotRunner {

    static {
        ApiContextInitializer.init();
    }

    private static boolean isApplicationRun = true;

    public static void main(String[] args) {
        final var bot = new TelegramBotImpl();
        var tgBotThread = new Thread(() -> {
            try {
                new TelegramBotsApi()
                        .registerBot(bot);
            } catch (TelegramApiRequestException e) {
                e.printStackTrace();
                isApplicationRun = false;
            }
        });
        tgBotThread.setDaemon(true);
        tgBotThread.start();
        try (final var serverSocket = new ServerSocket(8089)) {
            while (isApplicationRun) {
                try (final var clientSocket = serverSocket.accept()) {
//                    try (final var clientDataInputStream = clientSocket.getInputStream()) {
//                        var dataBufferedReader =
//                                new BufferedReader(
//                                        new InputStreamReader(clientDataInputStream));
//                        final var data = dataBufferedReader.readLine();
//                        if (data != null && data.equals("disable")) {
//                            isApplicationRun = false;
//                        }
//                    }
                    final var writer =
                            new BufferedWriter(
                                    new OutputStreamWriter(clientSocket.getOutputStream()));
                    writer.write("Али лох");
                    writer.flush();
                    isApplicationRun = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finish");
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

        private String getMessage(final String msg) {
            var lowerCaseMessage = msg.toLowerCase();
            final var keyboard = new ArrayList<KeyboardRow>();
            final var keyboardRow = new KeyboardRow();

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
            } else if (StringUtils.contains(lowerCaseMessage, "шишки")) {
                return "Наше все";
            } else if (StringUtils.contains(lowerCaseMessage, "питух") && StringUtils.contains(lowerCaseMessage, "админ")) {
                return "Сам петух \uD83D\uDC13";
            } else {
                return "Али гей";
            }
        }

        private String getRandomMessage() {
            final var random = new Random();
            return content.get(random.nextInt(3))
                    .get(random.nextInt(4));
        }

        private Map<Integer, List<String>> getCases() {
            final var data = new HashMap<Integer, List<String>>();
            final var firstCases = new ArrayList<String>();
            final var secondCases = new ArrayList<String>();
            final var thirdCases = new ArrayList<String>();
            final var fourthCases = new ArrayList<String>();
            firstCases.add("Бесспорно");
            firstCases.add("Предрешено");
            firstCases.add("Никаких сомнений");
            firstCases.add("Определенно да");
            firstCases.add("Можешь быть уверен в этом");
            data.put(0, firstCases);
            secondCases.add("Мне кажется — «да»");
            secondCases.add("Вероятнее всего");
            secondCases.add("Хорошие перспективы");
            secondCases.add("Знаки говорят — «да»");
            secondCases.add("Да");
            data.put(1, secondCases);
            thirdCases.add("Пока не ясно, попробуй снова");
            thirdCases.add("Спроси позже");
            thirdCases.add("Лучше не рассказывать");
            thirdCases.add("Сейчас нельзя предсказать");
            thirdCases.add("Сконцентрируйся и спроси опять");
            data.put(2, thirdCases);
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
