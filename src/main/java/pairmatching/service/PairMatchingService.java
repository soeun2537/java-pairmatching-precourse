package pairmatching.service;

import static pairmatching.constant.PathConstant.*;
import static pairmatching.constant.message.ErrorMessage.*;

import camp.nextstep.edu.missionutils.Randoms;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import pairmatching.model.Course;
import pairmatching.model.Level;
import pairmatching.model.Matching;
import pairmatching.model.Mission;
import pairmatching.repository.MatchingRepository;
import pairmatching.util.FileReader;

public class PairMatchingService {

    private final MatchingRepository matchingRepository;

    public PairMatchingService(MatchingRepository matchingRepository) {
        this.matchingRepository = matchingRepository;
    }

    public Matching match(List<String> courseAndLevelAndMission) {
        Matching matching = initializeMatching(courseAndLevelAndMission);
        Queue<String> crewsQueue = initializeCrews(matching);

        List<List<String>> pairs = new ArrayList<>();
        matchCrews(crewsQueue, pairs);
        matching.addPairs(pairs);

        matchingRepository.add(matching);
        return matching;
    }

    private static Matching initializeMatching(List<String> courseAndLevelAndMission) {
        return Matching.initialize(Course.findCourse(courseAndLevelAndMission.get(0)),
                Level.findLevel(courseAndLevelAndMission.get(1)),
                Mission.findMission(courseAndLevelAndMission.get(2)));
    }

    private static Queue<String> initializeCrews(Matching matching) {
        List<String> crews = returnCrews(matching);
        return new LinkedList<>(Randoms.shuffle(crews));
    }

    private static void matchCrews(Queue<String> crewsQueue, List<List<String>> pairs) {
        while (!crewsQueue.isEmpty()) {
            List<String> pair = new ArrayList<>();
            if (crewsQueue.size() == 3) {
                handleQueue(crewsQueue, pair);
                handleQueue(crewsQueue, pair);
                handleQueue(crewsQueue, pair);
                pairs.add(pair);
                break;
            }
            handleQueue(crewsQueue, pair);
            handleQueue(crewsQueue, pair);
            pairs.add(pair);
        }
    }

    private static void handleQueue(Queue<String> queue, List<String> pair) {
        String element = queue.element();
        pair.add(element);
        queue.remove(element);
    }

    private static List<String> returnCrews(Matching matching) {
        if (matching.getCourse().equals(Course.백엔드)) {
            return FileReader.readFile(BACKEND_CREW_FILE_PATH.getPath());
        }
        return FileReader.readFile(FRONTEND_CREW_FILE_PATH.getPath());
    }

    public boolean isExist(List<String> courseAndLevelAndMission) {
        return matchingRepository.isExist(courseAndLevelAndMission);
    }

    public void remove(List<String> courseAndLevelAndMission) {
        matchingRepository.remove(courseAndLevelAndMission);
    }

    public Matching findMatching(List<String> courseAndLevelAndMission) {
        if (matchingRepository.isExist(courseAndLevelAndMission)) {
            return matchingRepository.findMatching(courseAndLevelAndMission);
        }
        throw new IllegalArgumentException(NOT_FOUND_RECORD.getMessage());
    }

    public void resetMatching() {
        matchingRepository.reset();
    }
}
