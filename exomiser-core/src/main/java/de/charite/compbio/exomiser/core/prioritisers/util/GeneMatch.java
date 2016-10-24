/*
 * The Exomiser - A tool to annotate and prioritize variants
 *
 * Copyright (C) 2012 - 2016  Charite Universitätsmedizin Berlin and Genome Research Ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.charite.compbio.exomiser.core.prioritisers.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.charite.compbio.exomiser.core.model.ModelPhenotypeMatch;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class GeneMatch {

    public static final GeneMatch NO_HIT = new GeneMatch(0, 0, 0, Collections.emptyList());

    private final Integer queryGeneId;
    private final Integer matchGeneId;
    private final double score;
    private final List<ModelPhenotypeMatch> bestMatchModels;

    private GeneMatch(Integer queryGeneId, Integer matchGeneId, double score, List<ModelPhenotypeMatch> bestMatchModels) {
        this.queryGeneId = queryGeneId;
        this.matchGeneId = matchGeneId;
        this.score = score;
        this.bestMatchModels = ImmutableList.copyOf(bestMatchModels);
    }

    public Integer getQueryGeneId() {
        return queryGeneId;
    }

    public Integer getMatchGeneId() {
        return matchGeneId;
    }

    public double getScore() {
        return score;
    }

    public List<ModelPhenotypeMatch> getBestMatchModels() {
        return bestMatchModels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneMatch)) return false;
        GeneMatch geneMatch = (GeneMatch) o;
        return Double.compare(geneMatch.score, score) == 0 &&
                Objects.equals(queryGeneId, geneMatch.queryGeneId) &&
                Objects.equals(matchGeneId, geneMatch.matchGeneId) &&
                Objects.equals(bestMatchModels, geneMatch.bestMatchModels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryGeneId, matchGeneId, score, bestMatchModels);
    }

    @Override
    public String toString() {
        return "GeneMatch{" +
                "queryGeneId=" + queryGeneId +
                ", matchGeneId=" + matchGeneId +
                ", score=" + score +
                ", bestMatchModels=" + bestMatchModels +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer queryGeneId = 0;
        private Integer matchGeneId = 0;
        private double score = 0;
        private List<ModelPhenotypeMatch> bestMatchModels = Lists.newArrayList();


        public Builder queryGeneId(Integer queryGeneId) {
            this.queryGeneId = queryGeneId;
            return this;
        }

        public Builder matchGeneId(Integer matchGeneId) {
            this.matchGeneId = matchGeneId;
            return this;
        }

        public Builder score(double score) {
            this.score = score;
            return this;
        }

        public Builder bestMatchModels(List<ModelPhenotypeMatch> bestMatchModels) {
            this.bestMatchModels = bestMatchModels;
            return this;
        }

        public GeneMatch build() {
            return new GeneMatch(queryGeneId, matchGeneId, score, bestMatchModels);
        }

    }
}