import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiDelimiterSplitExample {

    public String removeSpecialCharacter(String inputString) {
        return inputString.replaceAll("[^a-zA-Z0-9+\\-. ]", "");
    }

    public String replaceSpaces(String inputString) {
        return inputString.replaceAll("\\s", " ").replaceAll(" {2,}", " ");
    }

    public List<String> getWordList(List<String> delimiters, String inputString) {

        // 문자열을 분리하여 저장할 리스트
        List<String> wordList = new ArrayList<>();

        // 정규표현식을 사용하여 구분자로 문자열을 분리
        StringJoiner stringJoiner = new StringJoiner("|");

        for (String delimiter : delimiters) {
            stringJoiner.add(delimiter);
        }

        String regex = "\\b(" + stringJoiner +")\\b";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputString);

        // 구분자로 문자열을 분리하고, 각 잘려진 문자열과 구분자를 wordList 컬렉션에 저장
        int lastMatchEnd = 0;
        while (matcher.find()) {
            String separatedString = inputString.substring(lastMatchEnd, matcher.start());
            String trimmedString = separatedString.trim();
            if (trimmedString.length() > 0) {
                wordList.add(trimmedString.trim());
            }
            wordList.add(matcher.group()); // 구분자도 함께 저장
            lastMatchEnd = matcher.end();
        }

        // 마지막 구분자 이후의 문자열을 wordList 컬렉션에 저장
        if (lastMatchEnd < inputString.length()) {
            String separatedString = inputString.substring(lastMatchEnd);
            String trimmedString = separatedString.trim();
            if (trimmedString.length() > 0) {
                wordList.add(trimmedString.trim());
            }
        }

        return wordList;
    }

    public boolean validate(List<String> delimiters, List<String> wordList) {

        for (int i = 0; i < wordList.size(); i++) {

            String word = wordList.get(i);

            if (i % 2 == 0) {
                for (String delimiter : delimiters) {
                    if (word.equalsIgnoreCase(delimiter)) {
                        return false;
                    }
                }
            } else {
                boolean matched = false;
                for (String delimiter : delimiters) {
                    if (word.equalsIgnoreCase(delimiter)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
        }

        return true;
    }


    public static void main(String[] args) {
        MultiDelimiterSplitExample main = new MultiDelimiterSplitExample();

        // Memory DB
        Map<String, String> db = new HashMap<>();
        db.put("apple", "APPLE");
        db.put("banana OR grape", "BANANA GRAPE");
        db.put("keil", "KEIL");
        db.put("keil+-", "KEIL-2.0");

        // 입력 문자열
        String inputString = "(apple) and 'banana' OR \"grape\" and <orange>, AND {}[]keil!@#$%^&*()_+-/=\\`";

        System.out.println("입력 문자열: ");
        System.out.println(inputString);
        System.out.println();

        // 연속된 공백 제거
        inputString = main.replaceSpaces(inputString);

        // 특수 문자 제거
        inputString = main.removeSpecialCharacter(inputString);

        System.out.println("전처리 문자열: ");
        System.out.println(inputString);
        System.out.println();

        // 문자열 분리
        List<String> delimiters = List.of("and", "or");
        List<String> wordList = main.getWordList(delimiters, inputString);
        System.out.println("분리된 문자열:");
        for (String s : wordList) {
            System.out.println(s);
        }
        System.out.println();

        // 구분자 사이에 문자열이 있는지 확인
        boolean validated = main.validate(delimiters, wordList);

        if (validated) {

            List<String> convertedWordList = new ArrayList<>();

            for (int i = 0; i < wordList.size(); i++) {

                String word = wordList.get(i);

                if (i % 2 == 1) {
                    convertedWordList.add(word);
                    continue;
                }

                // DB 에서 license 조회
                if (db.containsKey(word)) {
                    String convertedWord = db.get(word);
                    convertedWordList.add(convertedWord);
                } else {
                    boolean found = false;

                    StringJoiner stringJoiner = new StringJoiner(" ");
                    stringJoiner.add(word);

                    for (int j = i + 1; j < wordList.size(); j++) {

                        String nextWord = wordList.get(j);
                        stringJoiner.add(nextWord);

                        if (j % 2 == 0) {

                            String joinedWord = stringJoiner.toString();
                            if (db.containsKey(joinedWord)) {
                                String convertedWord = db.get(joinedWord);
                                convertedWordList.add(convertedWord);

                                i = j;
                                found = true;
                                break;
                            }
                        }
                    }

                    // DB 에 없을 경우
                    if (!found) {
                        convertedWordList.add(word);
                    }
                }
            }

            System.out.println("변환된 문자열: ");
            for (String s : convertedWordList) {
                System.out.println(s);
            }
        }
    }
}
