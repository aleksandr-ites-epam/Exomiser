/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2017 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.monarchinitiative.exomiser.db.parsers;

import org.monarchinitiative.exomiser.db.resources.Resource;
import org.monarchinitiative.exomiser.db.resources.ResourceOperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class designed to parseResource in protein - protein interaction data from
 * STRING. This is the data used to perform Random Walk analysis in ExomeWalker.
 * However, we also want to display all protein interactions of distance one and
 * two in the HTML results page, and we will store PPI data in the SQL table
 * string for this purpose.
 * <P>
 * Before this class is called by {@link exomizer.PopulateExomiserDatabase}, you
 * need to download the file <b>protein.links.detailed.v9.1.txt.gz</b> from the
 * STRING database und uncompress it. Then, go to the <b>scripts</b> directory
 * in the Exomiser project, and run the R script <b>downloadEns2Entrez.R</b>.
 * This script will download a mapping between Ensembl ids (which are used by
 * STRING), and Entrez Gene ids (which are used by the Exomiser application).
 * Note that we will directly import the entrezGene to gene symbol data into the
 * database, as a table called entrez2sym.
 *
 * @see <a href="http://string-db.org/">STRING database</a>
 * @author Peter Robinson
 * @version 0.05 (15 Feb, 2014).
 */
public class StringParser implements ResourceParser {

    private static final Logger logger = LoggerFactory.getLogger(StringParser.class);

    private final Map<String, List<Integer>> ensembl2EntrezGene;

    private Set<Interaction> interactionSet = null;

    public StringParser(Map<String, List<Integer>> ensembl2EntrezGene) {
        this.ensembl2EntrezGene = ensembl2EntrezGene;
        this.interactionSet = new HashSet<>();
    }

    /**
     * A simple struct-like class representing an interaction.
     */
    class Interaction {

        int entrezGeneA;
        int entrezGeneB;
        int score;

        public Interaction(int A, int B, int sc) {
            this.entrezGeneA = A;
            this.entrezGeneB = B;
            this.score = sc;
        }

        public String getDumpLine() {
            return String.format("%d|%d|%d", entrezGeneA, entrezGeneB, score);
        }

        /**
         * We regard two interaction objects as being equal if both of the
         * interactants are the same. Note we are not interested in the score
         * and will take one or other of the scores arbitrarily if we find
         * objects that are equal like this while constructing the hashmap of
         * interactions.
         */
        @Override
        public boolean equals(Object obj) {

            Interaction other = (Interaction) obj;
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (other.entrezGeneA == this.entrezGeneA && other.entrezGeneB == this.entrezGeneB) {
                return true;
            }
            return other.entrezGeneB == this.entrezGeneA && other.entrezGeneA == this.entrezGeneB;
        }

        @Override
        public int hashCode() {
            int x = 37;
            x += 17 * entrezGeneA;
            x += 17 * entrezGeneB;
            return x + 13;
        }

    }

    /**
     * This function does the actual work of parsing the STRING file.
     *
     * @param resource
     * @param inDir
     * @param outDir
     * @return
     */
    @Override
    public void parseResource(Resource resource, Path inDir, Path outDir) {

        Path inFile = inDir.resolve(resource.getExtractedFileName());
        Path outFile = outDir.resolve(resource.getParsedFileName());

        logger.info("Parsing {} file: {}. Writing out to: {}", resource.getName(), inFile, outFile);

        ResourceOperationStatus status;

        try (BufferedReader reader = Files.newBufferedReader(inFile, Charset.defaultCharset());
             BufferedWriter writer = Files.newBufferedWriter(outFile, Charset.defaultCharset())) {

            //there is a header line, so we'll read it here before parsin the rest
            // the header is: 'protein1 protein2 combined_score'
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\s+");
                String p1 = null, p2 = null;
                if (split[0].substring(0, 5).equals("9606.")) {
                    p1 = split[0].substring(5);
                } else {
                    logger.error("Malformed protein (p1): {}", line);
                    continue;
                }
                if (split[1].substring(0, 5).equals("9606.")) {
                    p2 = split[1].substring(5);
                } else {
                    logger.error("Malformed protein (p2): {}", line);
                    continue;
                }
                Integer score = null;
                try {
                    score = Integer.parseInt(split[2]);
                } catch (NumberFormatException e) {
                    logger.error("Malformed score: {} (could not parse field: '{}')", line, split[2]);
                    continue;
                }
//                logger.info(p1+":"+p2+":"+score);

                List<Integer> e1 = this.ensembl2EntrezGene.get(p1);
                List<Integer> e2 = this.ensembl2EntrezGene.get(p2);
                if (e1 == null || e2 == null) {
                    /* cannot find entrezgene id, just skip */
                    continue;
                }
//                logger.info(p1+":"+p2+":"+score);
                if (score < 700) {
                    /* Note that STRING high-confidence scores have a score
                     of at least 0.700 (which is stored as 700 in this file). */
                    continue;
                }
                for (Integer a : e1) {
                    for (Integer b : e2) {
                        Interaction ita = new Interaction(a, b, score);
                        //System.out.println(a + " / " + b + "(" + score + ")");
                        if (!this.interactionSet.contains(ita)) {
                            writer.write(String.format("%s|%s|%s", a, b, score));
                            writer.newLine();
                            this.interactionSet.add(ita);
                        }
                    }
                }
            }
            status = ResourceOperationStatus.SUCCESS;
        } catch (FileNotFoundException ex) {
            logger.error("", ex);
            status = ResourceOperationStatus.FILE_NOT_FOUND;
        } catch (IOException ex) {
            logger.error("", ex);
            status = ResourceOperationStatus.FAILURE;
        }
        resource.setParseStatus(status);
        logger.info("{}", status);
    }

}
