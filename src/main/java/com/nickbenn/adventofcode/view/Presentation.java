/*
 *  Copyright 2023 Nicholas Bennett.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nickbenn.adventofcode.view;

import java.util.ResourceBundle;

/**
 * Utility class declaring constants read from (and for reading from) {@link ResourceBundle}
 * strings, suitable for use in console-mode output.
 */
public class Presentation {

  /** Base name of {@link java.util.ResourceBundle} containing user-facing strings. */
  public static final String BASE_BUNDLE_NAME = "strings";
  /** {@link ResourceBundle} with the base name specified by {@link #BASE_BUNDLE_NAME}. */
  public static final ResourceBundle STRINGS_BUNDLE = ResourceBundle.getBundle(BASE_BUNDLE_NAME);

  /** Property key of a format string usable for displaying day # and numeric solution value. */
  public static final String NUMERIC_SOLUTION_FORMAT_KEY = "numeric_solution_format";

  /** Value of the {@link #NUMERIC_SOLUTION_FORMAT_KEY} property of {@link #STRINGS_BUNDLE}. */
  public static final String NUMERIC_SOLUTION_FORMAT =
      STRINGS_BUNDLE.getString(NUMERIC_SOLUTION_FORMAT_KEY);

}
