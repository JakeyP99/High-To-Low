package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuizWildCardsFragment extends WildCardsFragments {
    public static final WildCardHeadings[] defaultQuizWildCards = {

            // Science Quiz Questions
            new WildCardHeadings("Quiz! What is the deepest ocean on Earth?", 10, true, true, "Pacific Ocean", "Science"),
            new WildCardHeadings("Quiz! How many legs does a spider have?", 10, true, true, "8", "Science"),
            new WildCardHeadings("Quiz! How many sides does a heptagon have?", 10, true, true, "7", "Science"),
            new WildCardHeadings("Quiz! What is the largest planet in our solar system?", 10, true, true, "Jupiter", "Science"),
            new WildCardHeadings("Quiz! Can you name all seven colors of the rainbow in order?", 10, true, true, "Red, Orange, Yellow, Green, Blue, Indigo, Violet", "Science"),
            new WildCardHeadings("Quiz! What is the formula for calculating the area of a circle?", 10, true, true, "πr^2", "Science"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true, "212", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for silver on the periodic table?", 10, true, true, "Ag", "Science"),
            new WildCardHeadings("Quiz! What is the chemical formula for glucose?", 10, true, true, "C6H12O6", "Science"),
            new WildCardHeadings("Quiz! How many chromosomes are in a human body cell?", 10, true, true, "46", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true, "He", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for oxygen on the periodic table?", 10, true, true, "O", "Science"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Celsius?", 10, true, true, "100", "Science"),
            new WildCardHeadings("Quiz! Who developed the theory of general relativity?", 10, true, true, "Albert Einstein", "Science"),
            new WildCardHeadings("Quiz! What is the atomic number of hydrogen?", 10, true, true, "1", "Science"),
            new WildCardHeadings("Quiz! What is the largest organ in the human body?", 10, true, true, "Skin", "Science"),
            new WildCardHeadings("Quiz! How many chromosomes are in a human body cell?", 10, true, true, "46", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true, "He", "Science"),
            new WildCardHeadings("Quiz! Who discovered penicillin?", 10, true, true, "Alexander Fleming", "Science"),
            new WildCardHeadings("Quiz! What is the formula for calculating the area of a circle?", 10, true, true, "πr^2", "Science"),
            new WildCardHeadings("Quiz! What is the largest species of shark?", 10, true, true, "Whale Shark", "Science"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true, "212", "Science"),
            new WildCardHeadings("Quiz! Who is known as the 'father of modern chemistry'?", 10, true, true, "Antoine Lavoisier", "Science"),
            new WildCardHeadings("Quiz! What is the formula for calculating density?", 10, true, true, "Density = Mass / Volume", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for iron on the periodic table?", 10, true, true, "Fe", "Science"),
            new WildCardHeadings("Quiz! Who proposed the theory of evolution by natural selection?", 10, true, true, "Charles Darwin", "Science"),
            new WildCardHeadings("Quiz! What is the largest moon in the solar system?", 10, true, true, "Ganymede", "Science"),
            new WildCardHeadings("Quiz! What is the freezing point of water in Celsius?", 10, true, true, "0", "Science"),
            new WildCardHeadings("Quiz! Who discovered the law of gravity?", 10, true, true, "Isaac Newton", "Science"),
            new WildCardHeadings("Quiz! What is the chemical formula for glucose?", 10, true, true, "C6H12O6", "Science"),
            new WildCardHeadings("Quiz! Who invented the first practical telephone?", 10, true, true, "Alexander Graham Bell", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for sodium on the periodic table?", 20, true, true, "Na", "Science"),
            new WildCardHeadings("Quiz! What is the process by which plants release water vapor through their leaves?", 20, true, true, "Transpiration", "Science"),
            new WildCardHeadings("Quiz! What is the unit of measurement for energy?", 20, true, true, "Joule (J)", "Science"),
            new WildCardHeadings("Quiz! What is the hardest substance in the human body?", 20, true, true, "Tooth enamel", "Science"),
            new WildCardHeadings("Quiz! What is the chemical formula for sulfuric acid?", 20, true, true, "H2SO4", "Science"),
            new WildCardHeadings("Quiz! What is the process of a gas turning into a liquid?", 20, true, true, "Condensation", "Science"),
            new WildCardHeadings("Quiz! Which gas makes up about 78% of the Earth's atmosphere?", 20, true, true, "Nitrogen (N2)", "Science"),
            new WildCardHeadings("Quiz! What is the unit of measurement for frequency?", 20, true, true, "Hertz (Hz)", "Science"),
            new WildCardHeadings("Quiz! What is the process of a solid turning directly into a gas?", 20, true, true, "Sublimation", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for copper on the periodic table?", 20, true, true, "Cu", "Science"),
            new WildCardHeadings("Quiz! What is the name for the study of fossils?", 20, true, true, "Paleontology", "Science"),
            new WildCardHeadings("Quiz! What is the unit of measurement for electric potential difference?", 20, true, true, "Volt (V)", "Science"),
            new WildCardHeadings("Quiz! Which gas do humans exhale during respiration?", 20, true, true, "Carbon dioxide (CO2)", "Science"),
            new WildCardHeadings("Quiz! What is the process of a liquid turning into a solid?", 20, true, true, "Freezing", "Science"),
            new WildCardHeadings("Quiz! What is the chemical formula for methane?", 20, true, true, "CH4", "Science"),
            new WildCardHeadings("Quiz! What is the process of energy transfer through electromagnetic waves?", 20, true, true, "Radiation", "Science"),
            new WildCardHeadings("Quiz! Which gas is produced during photosynthesis?", 20, true, true, "Oxygen (O2)", "Science"),
            new WildCardHeadings("Quiz! What is the unit of measurement for electric resistance?", 20, true, true, "Ohm (Ω)", "Science"),
            new WildCardHeadings("Quiz! What is the process of a liquid turning into a gas below its boiling point?", 20, true, true, "Evaporation", "Science"),
            new WildCardHeadings("Quiz! What is the chemical symbol for nitrogen on the periodic table?", 20, true, true, "N", "Science"),

// Geography Quiz Questions
            new WildCardHeadings("Quiz! What is the national animal of Canada?", 10, true, true, "Beaver", "Geography"),
            new WildCardHeadings("Quiz! How many time zones are there in the world?", 10, true, true, "24", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Brazil?", 10, true, true, "Brasília", "Geography"),
            new WildCardHeadings("Quiz! Which country is known as the 'Land of the Rising Sun'?", 10, true, true, "Japan", "Geography"),
            new WildCardHeadings("Quiz! In which continent is the Sahara Desert located?", 10, true, true, "Africa", "Geography"),
            new WildCardHeadings("Quiz! What is the largest country by land area?", 10, true, true, "Russia", "Geography"),
            new WildCardHeadings("Quiz! What is the highest mountain in the world?", 10, true, true, "Mount Everest", "Geography"),
            new WildCardHeadings("Quiz! What is the largest ocean?", 10, true, true, "Pacific Ocean", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Australia?", 10, true, true, "Canberra", "Geography"),
            new WildCardHeadings("Quiz! Which river is the longest in the world?", 10, true, true, "Nile", "Geography"),
            new WildCardHeadings("Quiz! What is the official language of Japan?", 10, true, true, "Japanese", "Geography"),
            new WildCardHeadings("Quiz! Which city is known as the 'Eternal City'?", 10, true, true, "Rome", "Geography"),
            new WildCardHeadings("Quiz! What is the largest desert in the world?", 10, true, true, "Antarctica", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Canada?", 10, true, true, "Ottawa", "Geography"),
            new WildCardHeadings("Quiz! Which country is famous for the Great Barrier Reef?", 10, true, true, "Australia", "Geography"),
            new WildCardHeadings("Quiz! What is the official language of Brazil?", 10, true, true, "Portuguese", "Geography"),
            new WildCardHeadings("Quiz! In which country is the Taj Mahal located?", 10, true, true, "India", "Geography"),
            new WildCardHeadings("Quiz! What is the largest lake in Africa?", 10, true, true, "Lake Victoria", "Geography"),
            new WildCardHeadings("Quiz! What is the currency of Japan?", 10, true, true, "Japanese yen", "Geography"),
            new WildCardHeadings("Quiz! In which country is Mount Kilimanjaro located?", 10, true, true, "Tanzania", "Geography"),
            new WildCardHeadings("Quiz! What is the smallest country in the world?", 10, true, true, "Vatican City", "Geography"),
            new WildCardHeadings("Quiz! Which country is known for the Pyramids of Giza?", 10, true, true, "Egypt", "Geography"),
            new WildCardHeadings("Quiz! Which country is the largest archipelago in the world?", 20, true, true, "Indonesia", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Spain?", 20, true, true, "Madrid", "Geography"),
            new WildCardHeadings("Quiz! Which river runs through the Grand Canyon in the United States?", 20, true, true, "Colorado River", "Geography"),
            new WildCardHeadings("Quiz! In which continent is the Andes mountain range located?", 20, true, true, "South America", "Geography"),
            new WildCardHeadings("Quiz! What is the largest island in the Mediterranean Sea?", 20, true, true, "Sicily", "Geography"),
            new WildCardHeadings("Quiz! Which African country is known as the 'Pearl of the Indian Ocean'?", 20, true, true, "Sri Lanka", "Geography"),
            new WildCardHeadings("Quiz! What is the official language of Germany?", 20, true, true, "German", "Geography"),
            new WildCardHeadings("Quiz! Which country is the world's leading producer of coffee?", 20, true, true, "Brazil", "Geography"),
            new WildCardHeadings("Quiz! What is the smallest continent in the world?", 20, true, true, "Australia", "Geography"),
            new WildCardHeadings("Quiz! Which river is known as the 'Mother River' in China?", 20, true, true, "Yangtze River", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of South Africa?", 20, true, true, "Pretoria", "Geography"),
            new WildCardHeadings("Quiz! Which country is the Land of a Thousand Lakes?", 20, true, true, "Finland", "Geography"),
            new WildCardHeadings("Quiz! In which U.S. state is the Grand Canyon located?", 20, true, true, "Arizona", "Geography"),
            new WildCardHeadings("Quiz! Which country is home to the famous rock formation Uluru (Ayers Rock)?", 20, true, true, "Australia", "Geography"),
            new WildCardHeadings("Quiz! What is the largest city in Canada?", 20, true, true, "Toronto", "Geography"),
            new WildCardHeadings("Quiz! Which river is the longest in Europe?", 20, true, true, "Volga River", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Argentina?", 20, true, true, "Buenos Aires", "Geography"),
            new WildCardHeadings("Quiz! Which country is known as the 'Land of Fire and Ice'?", 20, true, true, "Iceland", "Geography"),
            new WildCardHeadings("Quiz! What is the highest waterfall in the world?", 20, true, true, "Angel Falls", "Geography"),
            new WildCardHeadings("Quiz! Which city is located on two continents, Europe and Asia?", 20, true, true, "Istanbul", "Geography"),
            new WildCardHeadings("Quiz! Which country is known as the 'Land of the Midnight Sun'?", 10, true, true, "Norway", "Geography"),
            new WildCardHeadings("Quiz! What is the largest river in the world by discharge volume?", 10, true, true, "Amazon River", "Geography"),
            new WildCardHeadings("Quiz! Which country is famous for its tulip fields and windmills?", 10, true, true, "Netherlands", "Geography"),
            new WildCardHeadings("Quiz! What is the capital of Russia?", 10, true, true, "Moscow", "Geography"),
            new WildCardHeadings("Quiz! Which African country is known as the 'Pearl of Africa'?", 10, true, true, "Uganda", "Geography"),
            new WildCardHeadings("Quiz! In which mountain range is Mount Everest located?", 10, true, true, "Himalayas", "Geography"),
            new WildCardHeadings("Quiz! What is the largest hot desert in the world?", 10, true, true, "Sahara Desert", "Geography"),
            new WildCardHeadings("Quiz! Which country is famous for the Great Wall?", 10, true, true, "China", "Geography"),
            new WildCardHeadings("Quiz! In which continent is the Great Barrier Reef located?", 10, true, true, "Australia", "Geography"),


// History Quiz Questions
            new WildCardHeadings("Quiz! Who is the current Prime Minister of Japan?", 10, true, true, "Yoshihide Suga", "History"),
            new WildCardHeadings("Quiz! In which year did World War I begin?", 10, true, true, "1914", "History"),
            new WildCardHeadings("Quiz! Who was the first President of the United States?", 10, true, true, "George Washington", "History"),
            new WildCardHeadings("Quiz! In which year did the United States declare independence from Great Britain?", 10, true, true, "1776", "History"),
            new WildCardHeadings("Quiz! Who was the leader of the Soviet Union during World War II?", 10, true, true, "Joseph Stalin", "History"),
            new WildCardHeadings("Quiz! Who painted the Mona Lisa?", 10, true, true, "Leonardo da Vinci", "History"),
            new WildCardHeadings("Quiz! In which year did the Titanic sink?", 10, true, true, "1912", "History"),
            new WildCardHeadings("Quiz! Who wrote the play 'Romeo and Juliet'?", 10, true, true, "William Shakespeare", "History"),
            new WildCardHeadings("Quiz! In which year did the American Civil War end?", 10, true, true, "1865", "History"),
            new WildCardHeadings("Quiz! Who was the first female Prime Minister of the United Kingdom?", 10, true, true, "Margaret Thatcher", "History"),
            new WildCardHeadings("Quiz! In which country was Adolf Hitler born?", 10, true, true, "Austria", "History"),
            new WildCardHeadings("Quiz! What is the significance of the Magna Carta?", 10, true, true, "It established the principle of the rule of law", "History"),
            new WildCardHeadings("Quiz! Who was the last pharaoh of ancient Egypt?", 10, true, true, "Cleopatra", "History"),
            new WildCardHeadings("Quiz! In which year did the Berlin Wall fall?", 10, true, true, "1989", "History"),
            new WildCardHeadings("Quiz! Who was the first man to walk on the moon?", 10, true, true, "Neil Armstrong", "History"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true, "Vincent van Gogh", "History"),
            new WildCardHeadings("Quiz! In which year did World War II end?", 10, true, true, "1945", "History"),
            new WildCardHeadings("Quiz! Who wrote the novel 'Pride and Prejudice'?", 10, true, true, "Jane Austen", "History"),
            new WildCardHeadings("Quiz! What is the significance of the Declaration of Independence?", 10, true, true, "It announced the United States' independence from Great Britain", "History"),
            new WildCardHeadings("Quiz! Who was the first Emperor of Rome?", 10, true, true, "Augustus", "History"),
            new WildCardHeadings("Quiz! In which year did the Renaissance begin?", 10, true, true, "14th century", "History"),
            new WildCardHeadings("Quiz! Who was the founder of the Mongol Empire?", 20, true, true, "Genghis Khan", "History"),
            new WildCardHeadings("Quiz! In which year did the French Revolution begin?", 20, true, true, "1789", "History"),
            new WildCardHeadings("Quiz! Who was the first female pharaoh of ancient Egypt?", 20, true, true, "Hatshepsut", "History"),
            new WildCardHeadings("Quiz! In which year did the Industrial Revolution start?", 20, true, true, "1760", "History"),
            new WildCardHeadings("Quiz! Who was the first woman to fly solo across the Atlantic?", 20, true, true, "Amelia Earhart", "History"),
            new WildCardHeadings("Quiz! In which year did the Cuban Missile Crisis occur?", 20, true, true, "1962", "History"),
            new WildCardHeadings("Quiz! Who was the first African American President of the United States?", 20, true, true, "Barack Obama", "History"),
            new WildCardHeadings("Quiz! In which year did the Battle of Gettysburg take place during the American Civil War?", 20, true, true, "1863", "History"),
            new WildCardHeadings("Quiz! Who was the first woman to win a Nobel Prize?", 20, true, true, "Marie Curie", "History"),
            new WildCardHeadings("Quiz! In which year did the Russian Revolution occur?", 20, true, true, "1917", "History"),
            new WildCardHeadings("Quiz! Who was the first European explorer to reach India by sea?", 20, true, true, "Vasco da Gama", "History"),
            new WildCardHeadings("Quiz! In which year did the American Revolution end?", 20, true, true, "1783", "History"),
            new WildCardHeadings("Quiz! Who was the Roman general who led the conquest of Gaul?", 20, true, true, "Julius Caesar", "History"),
            new WildCardHeadings("Quiz! In which year did the Treaty of Versailles end World War I?", 20, true, true, "1919", "History"),
            new WildCardHeadings("Quiz! Who was the legendary king of Uruk in ancient Mesopotamia?", 20, true, true, "Gilgamesh", "History"),
            new WildCardHeadings("Quiz! In which year did the American colonies declare independence from Spain?", 20, true, true, "1810", "History"),
            new WildCardHeadings("Quiz! Who was the first Roman Emperor?", 20, true, true, "Augustus", "History"),
            new WildCardHeadings("Quiz! In which year did the Treaty of Paris end the American Revolutionary War?", 20, true, true, "1783", "History"),
            new WildCardHeadings("Quiz! Who was the leader of the Bolshevik Party during the October Revolution?", 20, true, true, "Vladimir Lenin", "History"),
            new WildCardHeadings("Quiz! In which year did the Black Death (Bubonic Plague) spread across Europe?", 20, true, true, "1347", "History"),
            new WildCardHeadings("Quiz! Who was the first female astronaut to travel to space?", 10, true, true, "Valentina Tereshkova", "History"),
            new WildCardHeadings("Quiz! In which year did the Spanish conquistador Hernán Cortés conquer the Aztec Empire?", 10, true, true, "1521", "History"),
            new WildCardHeadings("Quiz! Who was the ancient Egyptian queen who ruled alongside her husband, Pharaoh Akhenaten?", 10, true, true, "Nefertiti", "History"),
            new WildCardHeadings("Quiz! In which year did the Battle of Waterloo take place, leading to the defeat of Napoleon Bonaparte?", 10, true, true, "1815", "History"),
            new WildCardHeadings("Quiz! Who was the first President of the United States to be impeached?", 10, true, true, "Andrew Johnson", "History"),
            new WildCardHeadings("Quiz! In which year did the Great Fire of London devastate the city?", 10, true, true, "1666", "History"),
            new WildCardHeadings("Quiz! Who was the Mongol emperor who established the Yuan Dynasty in China?", 10, true, true, "Kublai Khan", "History"),
            new WildCardHeadings("Quiz! In which year did the Suez Canal open, connecting the Mediterranean Sea to the Red Sea?", 10, true, true, "1869", "History"),
            new WildCardHeadings("Quiz! Who was the leader of the Allied forces during World War II?", 10, true, true, "Dwight D. Eisenhower", "History"),
            new WildCardHeadings("Quiz! In which year did the Treaty of Tordesillas divide the New World between Spain and Portugal?", 10, true, true, "1494", "History"),


// Art/Music Quiz Questions
            new WildCardHeadings("Quiz! How many symphonies did Ludwig van Beethoven compose?", 10, true, true, "9", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Mona Lisa'?", 10, true, true, "Leonardo da Vinci", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous symphony 'Symphony No. 9'?", 10, true, true, "Ludwig van Beethoven", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true, "Vincent van Gogh", "Art/Music"),
            new WildCardHeadings("Quiz! Who wrote the play 'Hamlet'?", 10, true, true, "William Shakespeare", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Für Elise'?", 10, true, true, "Ludwig van Beethoven", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Picture of Dorian Gray'?", 10, true, true, "Oscar Wilde", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Last Supper'?", 10, true, true, "Leonardo da Vinci", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous ballet 'Swan Lake'?", 10, true, true, "Pyotr Ilyich Tchaikovsky", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead guitarist of the band Led Zeppelin?", 10, true, true, "Jimmy Page", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Scream'?", 10, true, true, "Edvard Munch", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous opera 'The Magic Flute'?", 10, true, true, "Wolfgang Amadeus Mozart", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'War and Peace'?", 10, true, true, "Leo Tolstoy", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'Guernica'?", 10, true, true, "Pablo Picasso", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous symphony 'Symphony No. 5'?", 10, true, true, "Ludwig van Beethoven", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead singer of the band The Rolling Stones?", 10, true, true, "Mick Jagger", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Birth of Venus'?", 10, true, true, "Sandro Botticelli", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Clair de Lune'?", 10, true, true, "Claude Debussy", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Great Gatsby'?", 10, true, true, "F. Scott Fitzgerald", "Art/Music"),
            new WildCardHeadings("Quiz! How many players are there on a soccer team?", 10, true, true, "11", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the Greek god of the sea?", 10, true, true, "Poseidon", "Art/Music"),
            new WildCardHeadings("Quiz! How many strings does a standard violin have?", 10, true, true, "4", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead vocalist of the band Queen?", 20, true, true, "Freddie Mercury", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous opera 'Carmen'?", 20, true, true, "Georges Bizet", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead guitarist of the band Guns N' Roses?", 20, true, true, "Slash", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'Girl with a Pearl Earring'?", 20, true, true, "Johannes Vermeer", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Moonlight Sonata'?", 20, true, true, "Ludwig van Beethoven", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the play 'Romeo and Juliet'?", 20, true, true, "William Shakespeare", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Creation of Adam'?", 20, true, true, "Michelangelo", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous ballet 'The Nutcracker'?", 20, true, true, "Pyotr Ilyich Tchaikovsky", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Da Vinci Code'?", 20, true, true, "Dan Brown", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'Water Lilies'?", 20, true, true, "Claude Monet", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous opera 'La Traviata'?", 20, true, true, "Giuseppe Verdi", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead singer of the band Aerosmith?", 20, true, true, "Steven Tyler", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Guernica'?", 20, true, true, "Pablo Picasso", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Nocturne in E-flat Major'?", 20, true, true, "Frederic Chopin", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Lord of the Rings'?", 20, true, true, "J.R.R. Tolkien", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Last Judgment'?", 20, true, true, "Michelangelo", "Art/Music"),
            new WildCardHeadings("Quiz! Who composed the famous opera 'The Marriage of Figaro'?", 20, true, true, "Wolfgang Amadeus Mozart", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead guitarist of the band AC/DC?", 20, true, true, "Angus Young", "Art/Music"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Persistence of Memory'?", 20, true, true, "Salvador Dalí", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead singer of the band Coldplay?", 10, true, true, "Chris Martin", "Art/Music"),
            new WildCardHeadings("Quiz! Which artist sang the song 'Crazy' and collaborated with Gnarls Barkley?", 10, true, true, "CeeLo Green", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the singer behind the hit 'Yeah!' featuring Lil Jon and Ludacris?", 10, true, true, "Usher", "Art/Music"),
            new WildCardHeadings("Quiz! Which artist performed 'I Want It That Way' with the Backstreet Boys?", 10, true, true, "AJ McLean", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the lead vocalist of the band Coldplay, known for songs like 'Yellow' and 'Fix You'?", 10, true, true, "Chris Martin", "Art/Music"),
            new WildCardHeadings("Quiz! Which artist released the song 'Poker Face' and is known for her unique fashion style?", 10, true, true, "Lady Gaga", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the rapper and producer behind 'Gold Digger' featuring Jamie Foxx?", 10, true, true, "Kanye West", "Art/Music"),
            new WildCardHeadings("Quiz! Which artist performed the hit song 'Dilemma' with Kelly Rowland?", 10, true, true, "Nelly", "Art/Music"),
            new WildCardHeadings("Quiz! Who is the British singer-songwriter behind 'Chasing Pavements' and 'Rolling in the Deep'?", 10, true, true, "Adele", "Art/Music"),
            new WildCardHeadings("Quiz! Which artist released the song 'Hey There Delilah' and is part of the band Plain White T's?", 10, true, true, "Tom Higgenson", "Art/Music")

    };

    public QuizWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.QUIZ;

        // Declare adapter as a field in the fragment
        adapter = new QuizWildCardsAdapter(defaultQuizWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setVisibility(View.GONE);

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnToggleAll.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        btnToggleAll.setLayoutParams(layoutParams);

        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }
}

