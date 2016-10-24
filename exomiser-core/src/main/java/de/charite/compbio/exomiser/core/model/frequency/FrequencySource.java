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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.charite.compbio.exomiser.core.model.frequency;

import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum describing where the frequency data has originated.
 * 
 * @author Damian Smedley <damian.smedley@sanger.ac.uk>
 * @author Jules  Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public enum FrequencySource {
    
    UNKNOWN("unknown"),
    //Frequencies from a local datasource
    LOCAL("Local"),
    
    //Thousand genomes http://www.1000genomes.org/ 
    THOUSAND_GENOMES("1000Genomes"),
    
    //ESP project http://evs.gs.washington.edu/EVS/
    ESP_AFRICAN_AMERICAN("ESP AA"),
    ESP_EUROPEAN_AMERICAN("ESP EA"),
    ESP_ALL("ESP All"),
        
    //ExAC project http://exac.broadinstitute.org/about
    EXAC_AFRICAN_INC_AFRICAN_AMERICAN("ExAC AFR"),
    EXAC_AMERICAN("ExAC AMR"),
    EXAC_EAST_ASIAN("ExAC EAS"),
    EXAC_SOUTH_ASIAN("ExAC SAS"),
    EXAC_FINNISH("ExAC FIN"),
    EXAC_NON_FINNISH_EUROPEAN("ExAC NFE"),
    EXAC_OTHER("ExAC OTH");
    
    public static final Set<FrequencySource> ALL_ESP_SOURCES = Sets.immutableEnumSet(EnumSet.range(ESP_AFRICAN_AMERICAN, ESP_ALL));

    public static final Set<FrequencySource> ALL_EXAC_SOURCES = Sets.immutableEnumSet(EnumSet.range(EXAC_AFRICAN_INC_AFRICAN_AMERICAN, EXAC_OTHER));
    
    public static final Set<FrequencySource> ALL_EXTERNAL_FREQ_SOURCES = Sets.immutableEnumSet(EnumSet.range(THOUSAND_GENOMES, EXAC_OTHER));
        
    private final String source;
            
    private FrequencySource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
    
}