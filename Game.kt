import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable
import java.util.*

class Game : Serializable{
    var isOver = false
    private var food: Int
    private var ammunition: Int
    private var clothing: Int
    private var misc: Int
    private var oxen: Int
    private var foodQuality = 0
    private var mileage = 0
    private var cash = 700
    private var clearedSouthPass = false
    private var clearedBlueMountains = false
    private var isInjured = false
    private var isIll = false
    private var isNearFort = true
    private val br = BufferedReader(InputStreamReader(System.`in`))
    private val date = OregonTrailDate(1847, 3, 29)
    private val rand = Random(System.currentTimeMillis())
    init {

        println("Welcome to the Oregon Trail!")
        println("Do you need instructions?(Y/N)")
        val c = br.readLine()
        if(c[0].toLowerCase() == 'y'){
            println("This program simulates a trip over the Oregon Trail")
            println("from Independence, Missouri to Oregon City, Oregon in 1847.")
            println("Your family of five will cover the 2000 mile Oregon Trail")
            println("in 5 - 6 months --- if you make it alive.")
            println()
            println("Press ENTER to continue...")
            br.readLine()
            println("You had saved $900 to spend for the trip, and you've just")
            println("paid $200 for a wagon.")
            println("You will need to spend the rest of your money on the")
            println("following items:")
            println()
            println("   Oxen - You can spend $200 - $300 on your team.")
            println("          The more you spend, the faster you'll go,")
            println("          because you'll have better animals.")
            println()
            println("   Food - The more you have, the less chance is there")
            println("          of getting sick")
            println()
            println("   Ammunition - $1 buys a belt of 50 bullets. You will")
            println("          need bullets for attack by animals and")
            println("          bandits, and for hunting food.")
            println()
            println("   Clothing - This is especially important for the cold")
            println("          weather you will encounter when crossing")
            println("          the mountains.")
            println()
            println("   Miscellaneous supplies - This includes medicine and")
            println("          other things you will need for sickness and")
            println("          emergency repairs.")
            println()
            println("Press ENTER to continue...")
            br.readLine()
            println("You can spend all your money before you start your trip")
            println("or you can save some of your cash to spend at forts along")
            println("the way when you run low. However, items cost more at the")
            println("forts. You can also go hunting along the way to get more")
            println("food.")
            println("Whenever you have to use your trusty rifle along the way,")
            println("you will see the words 'TYPE BANG!!'. The faster you type")
            println("in the word 'BANG' and hit the ENTER key, the better luck")
            println("you'll have with your gun.")
            println()
            println("Good luck!")
            println("Press ENTER to continue...")
            br.readLine()
            println("\n\n\n\n\n")
        }

        oxen = queryPlayer("How much do you want to spend on your oxen team?", 200, 300)
        cash -= oxen
        food = queryPlayer("How much do you want to spend on food?", max = cash)
        cash -= food
        ammunition = queryPlayer("How much do you want to spend on ammunition?", max = cash)*50
        cash -= ammunition/50
        clothing = queryPlayer("How much do you want to spend on clothing?", max = cash)
        cash -= clothing
        misc = queryPlayer("How much do you want to spend on miscellaneous supplies?", max = cash)
        cash -= misc

        println("After your initial purchases you have $$cash left.")
        println("Press ENTER to continue...")
        br.readLine()
        println("\n\n\n\n\n")
    }

    fun nextTurn(){
        if(mileage >= 2040){
            println("Hooray! You've finally arrived at Oregon City")
            println("after 2040 long miles!")
            isOver = true
            return
        }
        println()
        println("Today's date: $date")
        println("Your mileage: $mileage")
        println("Food: $food")
        println("Bullets: $ammunition")
        println("Clothing: $clothing")
        println("Misc. Supplies: $misc")
        println("Cash: $$cash")
        println()
        if(food < 12){
            println("You'd better buy some food or go hunting soon!")
        }
        if(isInjured || isIll){
            println("The doctors' bill is $20")
            cash -= 20
            if(cash < 0){
                println("but you can't afford to pay that much.")
                if(isIll) {
                    println("You have died of pneumonia.")
                }else{
                    println("You have died because of your injuries.")
                }
                isOver = true
                return
            }
            isInjured = false
            isIll = false
        }
        println()
        println("Press ENTER to continue...")
        br.readLine()
        println("\n\n\n")

        if(isNearFort){
            println("There is a fort nearby.")
            println("Do you want to (1) stop at the nearby fort, (2) hunt or (3) continue?")
            when(getNumber()){
                1 -> eventFort()
                2 -> eventHunt()
            }
        }else{
            println("Do you want to (1) hunt or (2) continue?")
            if(getNumber() == 1){
                eventHunt()
            }
        }

        if(food < 14){
            println("You don't have enough food and have died of starvation...")
            isOver = true
            return
        }

        foodQuality = eventEating()
        println("\n\n\n")
        mileage += 100 + (oxen - 220)/5 + (10*rand.nextFloat()).toInt()
        date.advance(7)
        isNearFort = rand.nextFloat() < .3

        if(rand.nextFloat() * 10 < (Math.pow((mileage/100 - 4).toDouble(), 2.0) + 72) / (Math.pow((mileage/100 - 4).toDouble(), 2.0) + 12)){
            eventRiders()
        }
        eventMisc()
        eventMountains()
    }

    private fun eventFort(){
        val fortFood = queryPlayer("How much do you want to spend on food?", max = cash)
        cash -= fortFood
        val fortAmmunition = queryPlayer("How much do you want to spend on ammunition?", max = cash)
        cash -= fortAmmunition
        val fortClothing = queryPlayer("How much do you want to spend on clothing?", max = cash)
        cash -= fortClothing
        val fortMisc = queryPlayer("How much do you want to spend on miscellaneous supplies?", max = cash)
        cash -= fortMisc

        food += (2.0/3 * fortFood).toInt()
        ammunition += (2.0/3 * fortAmmunition * 50).toInt()
        clothing += (2.0/3 * fortClothing).toInt()
        misc += (2.0/3 * fortMisc).toInt()
    }

    private fun eventHunt(){
        if(ammunition < 40){
            println("You need more ammunition to go hunting")
            return
        }
        val shootResult = subShooting()
        when {
            shootResult < 1 -> {
                println("Right between the eyes --- You got a big one!")
                food += 52 + (rand.nextFloat() * 6).toInt()
            }
            100*rand.nextFloat() > 13 * shootResult -> {
                println("Nice shot - right through the neck - feast tonight.")
                food += 48 - shootResult*3
            }
            else -> println("Sorry, no luck today.")
        }
        ammunition -= 10 + (rand.nextFloat() * 4).toInt()
        println("Press ENTER to continue...")
        br.readLine()
        println("\n\n\n")
    }

    private fun eventEating(): Int{
        var choice: Int
        do {
            println("Do you want to eat (1) poorly, (2) moderately or (3) well?")
            choice = getNumber()
        } while(choice < 1 || choice > 3)

        food -= 8 + 5*choice
        if(food < 0){
            println("You can't eat that well.")
            food += 8 + 5*choice
            return eventEating()
        }
        return choice
    }

    private fun eventRiders(){
        var isHostile = true
        val shootResult: Int
        print("Riders ahead. They ")
        if(rand.nextFloat() > .8){
            isHostile = false
            print("don't ")
        }
        println("look hostile.")
        println("TACTICS")
        println("(1) Run (2) Attack (3) Continue (4) Circle wagons")
        println("If you run you'll gain time but wear down your oxen.")
        println("If you circle you'll lose time.")
        println()
        if(rand.nextFloat() < .2){
            isHostile = !isHostile
        }
        val choice = getNumber()
        if(isHostile && choice == 1){
            mileage += 20
            misc -= 15
            ammunition -= 150
            oxen -= 40
            println("The Riders were hostile but you were able to escape.")
            println("It cost you supplies, ammunition and some of your oxens' health.")
            if(checkAmmo()) return
        }else if(!isHostile && choice == 1){
            mileage += 15
            oxen -= 10
            println("The Riders were friendly")
            println("But running cost you some of your oxens' health.")
        }else if(isHostile && (choice == 2 || choice == 4)){
            shootResult = subShooting()
            if(choice == 2) {
                ammunition -= shootResult * 40 + 80
            }else if(choice == 4){
                ammunition -= shootResult * 30 + 80
                mileage -= 25
            }
            if(checkAmmo()) return
            when {
                shootResult < 2 -> println("Nice shooting - You drove them off")
                shootResult < 5 -> {
                    println("Kinda slow with your Colt .45")
                    if(rand.nextFloat() > shootResult*.1){
                        println("You took an arrow to the knee.")
                        println("You should let a doctor take a look at that.")
                        isInjured = true
                    }
                }
                else -> {
                    isInjured = true
                    println("Lousy shot --- you got knifed.")
                    println("You'll have to go see a doctor.")
                }
            }
        }else if(!isHostile && choice == 2){
            ammunition -= 100
            mileage -= 5
            if(checkAmmo()) return
            println("The riders weren't hostile and are easily scared off.")
            println("You lose ammunition and time, however.")
        }else if(isHostile && choice == 3){
            if(rand.nextFloat() > .8){
                println("They did not attack")
                return
            }else{
                ammunition -= 150
                misc -= 15
                if(checkAmmo()) return
                println("You had to fight off the riders but got away.")
                println("Lost supplies and ammunition.")
            }
        }else if(!isHostile && choice == 3){
            return
        }else if(!isHostile && choice == 4){
            mileage -= 20
            println("The riders were friendly, but you lost time circling your wagons")
        }
        println("Press ENTER to continue...")
        br.readLine()
        println("\n\n\n")
    }

    private fun subShooting(): Int{
        println("TYPE BANG!!")
        val timeBefore = System.currentTimeMillis()
        val typed = br.readLine()
        val timeAfter = System.currentTimeMillis()
        var timeTotal = (timeAfter - timeBefore) / 1000
        if (typed.toUpperCase() != "BANG"){
            timeTotal = 7
        }
        println("Response time: ${timeTotal}s")
        return Math.min(timeTotal.toInt(), 7)
    }

    private fun checkAmmo(): Boolean{
        return if(ammunition < 0){
            println("You ran out of ammunition and were massacred by the riders.")
            isOver = true
            true
        }else {
            false
        }
    }

    private fun eventMisc(){
        val selection = (rand.nextFloat()*16).toInt()
        when(selection){
            0 -> eventMiniBreakdown()
            1 -> eventMiniArmBroken()
            2 -> eventMiniBadWeather()
            3 -> eventMiniBanditAttack()
            4 -> eventMiniContractDisease()
            5 -> eventMiniDirtyWater()
            6 -> eventMiniFire()
            7 -> eventMiniFog()
            8 -> eventMiniHailStorm()
            9 -> eventMiniHelpfulIndians()
            10 -> eventMiniOffspringLost()
            11 -> eventMiniOxInjured()
            12 -> eventMiniOxLost()
            13 -> eventMiniWildAnimals()
            14 -> eventMiniSnakebite()
            15 -> eventMiniRiver()
        }
        println("Press ENTER to continue...")
        br.readLine()
        println("\n\n\n")
    }

    private fun eventMiniBreakdown(){
        println("Wagon breaks down - lost time and supplies fixing it.")
        mileage -= 15 + (5*rand.nextFloat()).toInt()
        misc -= 8
    }

    private fun eventMiniOxInjured(){
        println("Ox injures leg.")
        println("This slows you down for the rest of the trip.")
        oxen -= 20
        mileage -= 25
    }

    private fun eventMiniArmBroken(){
        print("Bad luck, your ")
        if(rand.nextFloat() < .33){
            print("daughter broke her ")
        }else {
            print("son broke his ")
        }
        println("broke arm.")
        println("You had to stop and use supplies to make a sling.")
        mileage -= 5 + (4*rand.nextFloat()).toInt()
        misc -= 2 + (3*rand.nextFloat()).toInt()
    }

    private fun eventMiniOxLost(){
        println("Your ox wanders off.")
        println("You spent some time looking for it.")
        mileage -= 17
    }

    private fun eventMiniOffspringLost(){
        if(rand.nextFloat() < .67){
            println("Your daughter gets lost.")
        }else{
            println("Your son gets lost.")
        }
        println("Lost half a day searching.")
        mileage -= 10
    }

    private fun eventMiniDirtyWater(){
        println("Unsafe Water -- lost time looking for a clean spring.")
        mileage -= 2 + (10*rand.nextFloat()).toInt()
    }

    private fun eventMiniBadWeather(){
        if(mileage < 950){
            println("Heavy rain --- time and supplies lost")
            mileage -= 5 + (10*rand.nextFloat()).toInt()
            food -= 10
            ammunition -= 500
            misc -= 15
        }else{
            println("Cold Weather -- Brrrr...")
            if(clothing < 22 + 4*rand.nextFloat()){
                println("You don't have enough clothing to keep you warm.")
                subIllness()
            }else{
                println("Fortunately, you have enough clothes.")
            }
        }
    }

    private fun eventMiniHailStorm(){
        println("Hail storm! Supplies damaged.")
        mileage -= 5 + (10*rand.nextFloat()).toInt()
        ammunition -= 200
        misc -= 4 + (3*rand.nextFloat()).toInt()
    }

    private fun eventMiniContractDisease(){
        if(foodQuality == 1){
            subIllness()
        }else if(foodQuality == 2 || rand.nextFloat() > .25){
            subIllness()
        }else if(rand.nextFloat() > .5){
            subIllness()
        }
    }

    private fun eventMiniHelpfulIndians(){
        println("Helpful Indians show you where to find more food")
        food += 14
    }

    private fun eventMiniBanditAttack(){
        println("Bandits attack!")
        val shootResult = subShooting()
        ammunition -= 20*shootResult
        if(ammunition < 0){
            isOver = true
            println("You ran out of bullets and were killed by the bandits.")
            return
        }
        when {
            shootResult < 2 -> {
                println("Quickest draw outside of Dodge City!!")
                println("You got 'em!")
            }
            shootResult < 5 -> {
                println("You got shot in the leg.")
                println("Better have a doc take a look at the wounds.")
                isInjured = true
                misc -= 5
            }
            else -> {
                println("You were too slow.")
                println("They took lots of cash and one of your oxen")
                oxen -= 20
                cash /= 3
            }
        }
    }

    private fun eventMiniFire(){
        println("There was a fire in your wagons!")
        println("Food and supplies damaged.")
        food -= 40
        ammunition -= 400
        misc -= 3 + (8*rand.nextFloat()).toInt()
        mileage -= 15
    }

    private fun eventMiniFog(){
        println("You lose your way in heavy fog.")
        println("Time is lost.")
        mileage -= 10 + (5*rand.nextFloat()).toInt()
    }

    private fun eventMiniSnakebite(){
        println("You killed a venomous snake after it bit you!")
        ammunition -= 10
        misc -= 5
        if(misc < 0){
            println("You die of the snakebite since you have no medicine...")
            isOver = true
        }
    }

    private fun eventMiniRiver(){
        println("Wagon gets swamped fording a river.")
        println("Lost food and clothing.")
        food -= 30
        clothing -= 20
        mileage -= 20 + (20*rand.nextFloat()).toInt()
    }

    private fun eventMiniWildAnimals(){
        println("Wild animals attack!")
        val shootResult = subShooting()
        if(ammunition < 40){
            println("You didn't have enough bullets and were overpowered by wolves.")
            println("You have died.")
            isOver = true
            return
        }
        if(shootResult < 3){
            println("Nice shootin' pardner. They didn't get much.")
        }else{
            println("Little slow on the draw...")
            println("They got to your food and clothes!")
        }

        food -= shootResult*8
        clothing -= shootResult*4
        ammunition -= shootResult*20
    }

    private fun subIllness(){
        when {
            100 * rand.nextFloat() < 10 + 35*(foodQuality - 1) -> {
                println("Mild illness -- medicine used.")
                misc -= 2
                mileage -= 5
            }
            100 * rand.nextFloat() < 100 - 40/Math.pow(4.0, foodQuality - 1.0) -> {
                println("Bad illness -- medicine used.")
                misc -= 5
                mileage -= 5
            }
            else -> {
                println("Serious illness!")
                println("You must stop for medical attention")
                isIll = true
                misc -= 10
                mileage -= 5
            }
        }
    }

    private fun eventMountains(){
        if(mileage > 950){
            if(rand.nextFloat() * 8 < (Math.pow((mileage/100 - 4).toDouble(), 2.0) + 72) / (Math.pow((mileage/100 - 4).toDouble(), 2.0) + 12)){
                println("Rugged Mountains")
                when {
                    rand.nextFloat() < 0.1 -> {
                        println("You got lost --- valuable time lost finding the trail.")
                        mileage -= 60
                    }
                    rand.nextFloat() < 0.11 -> {
                        println("Wagon damaged!")
                        println("Lost time and supplies.")
                        mileage -= 20 + (rand.nextFloat()*30).toInt()
                        ammunition -= 200
                        misc -= 5
                    }
                    else -> {
                        println("The going gets slow...")
                        mileage -= 45 + (rand.nextFloat()/0.02).toInt()
                    }
                }

                if(!clearedSouthPass){
                    println("Entering the South Pass...")
                    if(rand.nextFloat() > 0.8){
                        println("You made it safely though the South Pass.")
                        println("No snow!")
                    }else{
                        println("There is a blizzard in the South Pass!")
                        println("Some supplies were destroyed...")
                        food -= 25
                        misc -= 10
                        ammunition -= 300
                        mileage -= 30 + (rand.nextFloat()*40).toInt()
                        if(clothing < 18 + rand.nextFloat()*2){
                            subIllness()
                        }
                    }
                    clearedSouthPass = true
                }
                if(!clearedBlueMountains && mileage > 1700){
                    println("Crossing the Blue Mountains...")
                    if(rand.nextFloat() > 0.7){
                        println("Nasty hail storm in the Blue Mountains.")
                        println("You lost quite a lot of supplies and time.")
                        food -= 25
                        misc -= 10
                        ammunition -= 300
                        mileage -= 30 + (rand.nextFloat()*40).toInt()
                    }else{
                        println("You crossed the blue mountains without any incidents.")
                    }
                    clearedBlueMountains = true
                }
                println("Press ENTER to continue...")
                br.readLine()
                println("\n\n\n")
            }
        }
    }

    private fun queryPlayer(query: String, min: Int = 0, max: Int = Int.MAX_VALUE): Int{
        var x: Int
        do {
            println(query)
            println("Min: $min | Max: $max")
            x = getNumber()
            if(x < min || x > max){
                println("Invalid input...")
            }
        } while(x < min || x > max)
        return x
    }

    private fun getNumber(): Int{
        return try {
            Integer.parseInt(br.readLine())
        }catch (e: Exception){
            println("Invalid input...")
            getNumber()
        }
    }
}