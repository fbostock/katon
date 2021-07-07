package fjdb.images;

import java.util.List;

interface MatchingAlgorithm {
    MatchResult findMatches(MainImage mainImage, List<CandidateImage> candidateImages);

    String algoName();
}
