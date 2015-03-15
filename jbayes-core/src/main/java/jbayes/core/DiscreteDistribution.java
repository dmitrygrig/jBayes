/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbayes.core;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DiscreteDistribution implements Distribution {

    private final List<Double> cpt;
    private List<Double> inference;

    public DiscreteDistribution(List<Double> cpt) {
        this.cpt = cpt;
    }

    public List<Double> getCpt() {
        return cpt;
    }

    @Override
    public List<Double> getInference() {
        return inference;
    }

    @Override
    public void setInference(List<Double> inference) {
        this.inference = inference;
    }

    public static DiscreteDistribution FromArray(Double... args) {
        return new DiscreteDistribution(Arrays.asList(args));
    }

    public static DiscreteDistribution FromArray(Integer... args) {
        List<Double> result = new ArrayList<>();
        for (Integer item : args) {
            result.add((double) item);
        }
        return new DiscreteDistribution(result);
    }

    @Override
    public String convertToString() {
        return Joiner.on(", ").join(getCpt().stream().map(x -> String.valueOf((x))).collect(Collectors.toList()));
    }
}
