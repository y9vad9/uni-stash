<!-- left sidebar with keyboards etc. -->
<div id="keyboards" class="keyboards well well-small">
<div class="loading active"></div>
<p><span class="label label-default">${_("kbd.memory")}:</span> <span id="memory">&hellip;</span></p>
<hr id="hr">

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_space_const">${_("kbd.space.title")} <span id="space">&hellip;</span></button>
<!-- Spaces, constants panel -->
<div id="kbd_space_const" class="kbd collapse">
    <div class="well well-small">
        <button
                class="btn btn-xs" data-inserts="SPACE = Z[];" data-back="2"
                title="${_("kbd.space.space.Z")}"
                >$\mathbb{Z}$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = Zp[];" data-back="2"
                title="${_("kbd.space.space.Zp")}"
                >$\mathbb{Z}p$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = Zp32[];" data-back="2"
                title="${_("kbd.space.space.Zp32")}"
                >$\mathbb{Z}p32$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = Z64[];" data-back="2"
                title="${_("kbd.space.space.Z64")}"
                >$\mathbb{Z}64$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = Q[];" data-back="2"
                title="${_("kbd.space.space.Q")}"
                >$\mathbb{Q}$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R[];" data-back="2"
                title="${_("kbd.space.space.R")}"
                >$\mathbb{R}$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64[];" data-back="2"
                title="${_("kbd.space.space.R64")}"
                >$\mathbb{R}64$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R128[];" data-back="2"
                title="${_("kbd.space.space.R128")}"
                >$\mathbb{R}128$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = C[];" data-back="2"
                title="${_("kbd.space.space.C")}"
                >$\mathbb{C}$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = C64[];" data-back="2"
                title="${_("kbd.space.space.C64")}"
                >$\mathbb{C}64$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = C128[];" data-back="2"
                title="${_("kbd.space.space.C128")}"
                >$\mathbb{C}128$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = CZ[];" data-back="2"
                title="${_("kbd.space.space.CZ")}"
                >$\mathbb{CZ}$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = CZp[];" data-back="2"
                title="${_("kbd.space.space.CZp")}"
                >$\mathbb{CZ}p$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = CZp32[];" data-back="2"
                title="${_("kbd.space.space.CZp32")}"
                >$\mathbb{CZ}p32$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = CZ64[];" data-back="2"
                title="${_("kbd.space.space.CZ64")}"
                >$\mathbb{CZ}64$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = CQ[];" data-back="2"
                title="${_("kbd.space.space.CQ")}"
                >$\mathbb{CQ}$
        </button>
    </div>
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_space_const_const">${_("kbd.space.const.title")}</p>

        <div id="kbd_space_const_const" class="collapse">
            <button
                    class="btn btn-xs" data-inserts="FLOATPOS = ;" data-back="1"
                    title="${_("kbd.space.const.FLOATPOS")}"
                    >FLOATPOS
            </button>
            <button
                    class="btn btn-xs" data-inserts="MachineEpsilonR64 = ;" data-back="1"
                    title="${_("kbd.space.const.MachineEpsilonR64")}"
                    >MachineEpsilonR64
            </button>
            <button
                    class="btn btn-xs" data-inserts="MachineEpsilonR = ;" data-back="1"
                    title="${_("kbd.space.const.MachineEpsilonR")}"
                    >MachineEpsilonR
            </button>
            <button
                    class="btn btn-xs" data-inserts="MachineEpsilonR = / ;" data-back="3"
                    title="${_("kbd.space.const.MachineEpsilonRAccuracy")}"
                    >MachineEpsilonR/Accuracy
            </button>
            <button
                    class="btn btn-xs" data-inserts="MOD = ;" data-back="1"
                    title="${_("kbd.space.const.MOD")}"
                    >MOD
            </button>
            <button
                    class="btn btn-xs" data-inserts="MOD32 = ;" data-back="1"
                    title="${_("kbd.space.const.MOD32")}"
                    >MOD32
            </button>
            <button
                    class="btn btn-xs" data-inserts="RADIAN = ;" data-back="1"
                    title="${_("kbd.space.const.RADIAN")}"
                    >RADIAN
            </button>
            <button
                    class="btn btn-xs" data-inserts="STEPBYSTEP = ;" data-back="1"
                    title="${_("kbd.space.const.STEPBYSTEP")}"
                    >STEPBYSTEP
            </button>
            <button
                    class="btn btn-xs" data-inserts="EXPAND = ;" data-back="1"
                    title="${_("kbd.space.const.EXPAND")}"
                    >EXPAND
            </button>
            <button
                    class="btn btn-xs" data-inserts="SUBSTITUTION = ;" data-back="1"
                    title="${_("kbd.space.const.SUBSTITUTION")}"
                    >SUBSTITUTION
            </button>
            <button
                    class="btn btn-xs" data-inserts="TIMEOUT = ;" data-back="1"
                    title="${_("kbd.space.const.TIMEOUT")}"
                    >TIMEOUT
            </button>
        </div>
    </div>
</div>


<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_symb">${_("kbd.symb.title")}</button>
<!-- Math symbols, greek alphabet panel -->
<div id="kbd_symb" class="kbd collapse">
<!-- Symbols of numbers, sets, inequolities -->
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_symb_symb0">${_("kbd.symb.symb0.title")}</p>

    <div id="kbd_symb_symb0" class="collapse">
        <button
                class="btn btn-xs" data-inserts="\i"
                title="${_("kbd.symb.symb0.i")}"
                >$i$
        </button>
        <button
                class="btn btn-xs" data-inserts="\e"
                title="${_("kbd.symb.symb0.e")}"
                >$e$
        </button>
        <button
                class="btn btn-xs" data-inserts="\pi"
                title="${_("kbd.symb.symb0.pi")}"
                >$\pi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\infty"
                title="${_("kbd.symb.symb0.infty")}"
                >$\infty$
        </button>
        <button
                class="btn btn-xs" data-inserts="\emptyset"
                title="${_("kbd.symb.symb0.emptyset")}"
                >$\emptyset$
       </button>
       <button
                class="btn btn-xs" data-inserts="\cup"
                title="${_("kbd.symb.symb0.cup")}"
                >$\cup$
        </button> 
        <button
                class="btn btn-xs" data-inserts="\setminus"
                title="${_("kbd.symb.symb0.setminus")}"
                >$\setminus$
       </button> 
       <button
                class="btn btn-xs" data-inserts="\triangle"
                title="${_("kbd.symb.symb0.triangle")}"
                >$\triangle$
        </button> 
       <button
                class="btn btn-xs" data-inserts="'"
                title="${_("kbd.symb.symb0.complement")}"
                >$'$
        </button> 
        <button
                class="btn btn-xs" data-inserts="\cap"
                title="${_("kbd.symb.symb0.cap")}"
                >$\cap$
       </button>  

        </button> 
                <button
                class="btn btn-xs" data-inserts="\(,\)" date-back="3"
                title="${_("kbd.symb.symb0.setOpen")}"
                >(a,b)
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\[,\]" date-back="3"
                title="${_("kbd.symb.symb0.setClosed")}"
                >[a,b]
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\(,\]" date-back="3"
                title="${_("kbd.symb.symb0.setHalfOpen")}"
                >(a,b]
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\[,\)" date-back="3"
                title="${_("kbd.symb.symb0.setHalfClosed")}"
                >[a,b)
        </button>

        </button> 
                <button
                class="btn btn-xs" data-inserts="\ge" date-back="0"
                title="${_("kbd.symb.symb0.ge")}"
                >$\ge$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\ne" date-back="0"
                title="${_("kbd.symb.symb0.ne")}"
                >$\ne$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="==" date-back="0"
                title="${_("kbd.symb.symb0.eq")}"
                >=
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\le" date-back="0"
                title="${_("kbd.symb.symb0.le")}"
                >$\le$
        </button>  
       </button> 
                <button
                class="btn btn-xs" data-inserts="\lor" date-back="0"
                title="${_("kbd.symb.symb0.lor")}"
                >$\lor$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\&" date-back="0"
                title="${_("kbd.symb.symb0.land")}"
                >$\&$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\neg" date-back="0"
                title="${_("kbd.symb.symb0.neg")}"
                >$\neg$
        </button> 
       </div>
     </div>
  
<!-- Symbols -->
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_symb_symb">${_("kbd.symb.symb.title")}</p>
    <div id="kbd_symb_symb" class="collapse">  
       <button
                class="btn btn-xs" data-inserts="\circ"
                title="${_("kbd.symb.symb.circ")}"
                >$\circ$
       </button>      
       <button
                class="btn btn-xs" data-inserts="\partial"
                title="${_("kbd.symb.symb.partial")}"
                >$\partial$
           </button>   
           <button
                class="btn btn-xs" data-inserts="\nabla"
                title="${_("kbd.symb.symb.nabla")}"
                >$\nabla$
            </button>
            <button
                class="btn btn-xs" data-inserts="\hbar"
                title="${_("kbd.symb.symb.hbar")}"
                >$\hbar$
            </button>     
              <button
                class="btn btn-xs" data-inserts="\to"
                title="${_("kbd.symb.symb.to")}"
                >$\to$
        </button>        
        <button
                class="btn btn-xs" data-inserts="\perp"
                title="${_("kbd.symb.symb.perp")}"
                >$\perp$
        </button>
        <button
                class="btn btn-xs" data-inserts="\parallel"
                title="${_("kbd.symb.symb.parallel")}"
                >$\parallel$
        </button>
        <button
                class="btn btn-xs" data-inserts="\angle"
                title="${_("kbd.symb.symb.angle")}"
                >$\angle$
        </button>
      <button
                class="btn btn-xs" data-inserts="\smile"
                title="${_("kbd.symb.symb.smile")}"
                >$\smile$
        </button>
      
            <button
                class="btn btn-xs" data-inserts="\equiv"
                title="${_("kbd.symb.symb.equiv")}"
                >$\equiv$
        </button>
      <button
                class="btn btn-xs" data-inserts="\square"
                title="${_("kbd.symb.symb.square")}"
                >$\square$
        </button>
            <button
                class="btn btn-xs" data-inserts="\blacksquare"
                title="${_("kbd.symb.symb.blacksquare")}"
                >$\blacksquare$
        </button>
      <button
                class="btn btn-xs" data-inserts="\approx"
                title="${_("kbd.symb.symb.approx")}"
                >$\approx$
        </button>
      <button
                class="btn btn-xs" data-inserts="\sim"
                title="${_("kbd.symb.symb.sim")}"
                >$\sim$
        </button>
            <button
                class="btn btn-xs" data-inserts="\in"
                title="${_("kbd.symb.symb.in")}"
                >$\in$
        </button>
            <button
                class="btn btn-xs" data-inserts="\notin"
                title="${_("kbd.symb.symb.notin")}"
                >$\notin$
        </button>
            <button
                class="btn btn-xs" data-inserts="\owns"
                title="${_("kbd.symb.symb.owns")}"
                >$\owns$
        </button>
            <button
                class="btn btn-xs" data-inserts="\subset"
                title="${_("kbd.symb.symb.subset")}"
                >$\subset$
        </button>
     <button
                class="btn btn-xs" data-inserts="\subseteq"
                title="${_("kbd.symb.symb.subseteq")}"
                >$\subseteq$
        </button>
           <button
                class="btn btn-xs" data-inserts="\supset"
                title="${_("kbd.symb.symb.supset")}"
                >$\supset$
        </button>
           <button
                class="btn btn-xs" data-inserts="\supseteq"
                title="${_("kbd.symb.symb.supseteq")}"
                >$\supseteq$
        </button>
           <button
                class="btn btn-xs" data-inserts="\exists"
                title="${_("kbd.symb.symb.exists")}"
                >$\exists$
        </button>
           <button
                class="btn btn-xs" data-inserts="\nexists"
                title="${_("kbd.symb.symb.nexists")}"
                >$\nexists$
        </button>
           <button
                class="btn btn-xs" data-inserts="\forall"
                title="${_("kbd.symb.symb.forall")}"
                >$\forall$
        </button>
                 <button
                class="btn btn-xs" data-inserts="\neg"
                title="${_("kbd.symb.symb.neg")}"
                >$\neg$
        </button>
                 <button
                class="btn btn-xs" data-inserts="\vee"
                title="${_("kbd.symb.symb.vee")}"
                >$\vee$
        </button>
         <button
                class="btn btn-xs" data-inserts="\wedge"
                title="${_("kbd.symb.symb.wedge")}"
                >$\wedge$
        </button>
         <button
                class="btn btn-xs" data-inserts="\oplus"
                title="${_("kbd.symb.symb.oplus")}"
                >$\oplus$
        </button>
      <button
                class="btn btn-xs" data-inserts="\otimes"
                title="${_("kbd.symb.symb.otimes")}"
                >$\otimes$
        </button>
            <button
                class="btn btn-xs" data-inserts="\hat"
                title="${_("kbd.symb.symb.hat")}"
                >$\hat a$
        </button>
            <button
                class="btn btn-xs" data-inserts="\bar"
                title="${_("kbd.symb.symb.bar")}"
                >$\bar a$
        </button>
            <button
                class="btn btn-xs" data-inserts="\tilde"
                title="${_("kbd.symb.symb.tilde")}"
                >$\tilde a$
        </button>
            <button
                class="btn btn-xs" data-inserts="\vec "
                title="${_("kbd.symb.symb.vec")}"
                >$\vec a$
        </button>
            <button
                class="btn btn-xs" data-inserts="\dot "
                title="${_("kbd.symb.symb.dot")}"
                >$\dot a$
        </button>
            <button
                class="btn btn-xs" data-inserts="\ddot "
                title="${_("kbd.symb.symb.ddot")}"
                >$\ddot a$
        </button>
        <button
                class="btn btn-xs" data-inserts="\widetilde{}"
                title="${_("kbd.symb.symb.widetilde")}"
                >$\widetilde{az}$
        </button>
        <button
                class="btn btn-xs" data-inserts="\widehat{}"
                title="${_("kbd.symb.symb.widehat")}"
                >$\widehat{az}$
        </button>
        <button
                class="btn btn-xs" data-inserts="\overline{}"
                title="${_("kbd.symb.symb.overline")}"
                >$\overline{az}$
        </button>
        <button
                class="btn btn-xs" data-inserts="\overrightarrow{}"
                title="${_("kbd.symb.symb.overrightarrow")}"
                >$\overrightarrow{az}$
        </button>    
        <button
                class="btn btn-xs" data-inserts="\underbrace{}"
                title="${_("kbd.symb.symb.underbrace")}"
                >$\underbrace{az}$
        </button> 
                <button
                class="btn btn-xs" data-inserts="\overbrace{}"
                title="${_("kbd.symb.symb.overbrace")}"
                >$\overbrace{az}$
        </button> 
                <button
                class="btn btn-xs" data-inserts="\frac{}{}" date-back="3"
                title="${_("kbd.symb.symb.frac")}"
                >$\frac{a}{b}$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\system(,)" date-back="2"
                title="${_("kbd.symb.symb.system")}"
                >$\{$
        </button>
        </button> 
                <button
                class="btn btn-xs" data-inserts="\systemOR(,)" date-back="2"
                title="${_("kbd.symb.symb.systemOR")}"
                >$[$
        </button>
    </div>
</div>
<!-- /Symbols -->
<!-- Greek lowercase -->
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_symb_greek_lower">${_("kbd.symb.greek.lower.title")}</p>

    <div id="kbd_symb_greek_lower" class="collapse">
        <button
                class="btn btn-xs" data-inserts="\alpha"
                title="${_("kbd.symb.greek.lower.alpha")}"
                >$\alpha$
        </button>
        <button
                class="btn btn-xs" data-inserts="\beta"
                title="${_("kbd.symb.greek.lower.beta")}"
                >$\beta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\gamma"
                title="${_("kbd.symb.greek.lower.gamma")}"
                >$\gamma$
        </button>
        <button
                class="btn btn-xs" data-inserts="\delta"
                title="${_("kbd.symb.greek.lower.delta")}"
                >$\delta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\epsilon"
                title="${_("kbd.symb.greek.lower.epsilon")}"
                >$\epsilon$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varepsilon"
                title="${_("kbd.symb.greek.lower.varepsilon")}"
                >$\varepsilon$
        </button>
        <button
                class="btn btn-xs" data-inserts="\zeta"
                title="${_("kbd.symb.greek.lower.zeta")}"
                >$\zeta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\eta"
                title="${_("kbd.symb.greek.lower.eta")}"
                >$\eta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\theta"
                title="${_("kbd.symb.greek.lower.theta")}"
                >$\theta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\vartheta"
                title="${_("kbd.symb.greek.lower.vartheta")}"
                >$\vartheta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\iota"
                title="${_("kbd.symb.greek.lower.iota")}"
                >$\iota$
        </button>
        <button
                class="btn btn-xs" data-inserts="\kappa"
                title="${_("kbd.symb.greek.lower.kappa")}"
                >$\kappa$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varkappa"
                title="${_("kbd.symb.greek.lower.varkappa")}"
                >$\varkappa$
        </button>
        <button
                class="btn btn-xs" data-inserts="\lambda"
                title="${_("kbd.symb.greek.lower.lambda")}"
                >$\lambda$
        </button>
        <button
                class="btn btn-xs" data-inserts="\mu"
                title="${_("kbd.symb.greek.lower.mu")}"
                >$\mu$
        </button>
        <button
                class="btn btn-xs" data-inserts="\nu"
                title="${_("kbd.symb.greek.lower.nu")}"
                >$\nu$
        </button>
        <button
                class="btn btn-xs" data-inserts="\xi"
                title="${_("kbd.symb.greek.lower.xi")}"
                >$\xi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\pi"
                title="${_("kbd.symb.greek.lower.pi")}"
                >$\pi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varpi"
                title="${_("kbd.symb.greek.lower.varpi")}"
                >$\varpi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\rho"
                title="${_("kbd.symb.greek.lower.rho")}"
                >$\rho$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varrho"
                title="${_("kbd.symb.greek.lower.varrho")}"
                >$\varrho$
        </button>
        <button
                class="btn btn-xs" data-inserts="\sigma"
                title="${_("kbd.symb.greek.lower.sigma")}"
                >$\sigma$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varsigma"
                title="${_("kbd.symb.greek.lower.varsigma")}"
                >$\varsigma$
        </button>
        <button
                class="btn btn-xs" data-inserts="\tau"
                title="${_("kbd.symb.greek.lower.tau")}"
                >$\tau$
        </button>
        <button
                class="btn btn-xs" data-inserts="\upsilon"
                title="${_("kbd.symb.greek.lower.upsilon")}"
                >$\upsilon$
        </button>
        <button
                class="btn btn-xs" data-inserts="\phi"
                title="${_("kbd.symb.greek.lower.phi")}"
                >$\phi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\varphi"
                title="${_("kbd.symb.greek.lower.varphi")}"
                >$\varphi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\chi"
                title="${_("kbd.symb.greek.lower.chi")}"
                >$\chi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\psi"
                title="${_("kbd.symb.greek.lower.psi")}"
                >$\psi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\omega"
                title="${_("kbd.symb.greek.lower.omega")}"
                >$\omega$
        </button>
    </div>
</div>
<!-- /Greek lowercase -->
<!-- Greek uppercase -->
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_symb_greek_upper">${_("kbd.symb.greek.upper.title")}</p>

    <div id="kbd_symb_greek_upper" class="collapse">
        <button
                class="btn btn-xs" data-inserts="\Gamma"
                title="${_("kbd.symb.greek.upper.Gamma")}"
                >$\Gamma$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Delta"
                title="${_("kbd.symb.greek.upper.Delta")}"
                >$\Delta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Theta"
                title="${_("kbd.symb.greek.upper.Theta")}"
                >$\Theta$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Lambda"
                title="${_("kbd.symb.greek.upper.Lambda")}"
                >$\Lambda$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Xi"
                title="${_("kbd.symb.greek.upper.Xi")}"
                >$\Xi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Pi"
                title="${_("kbd.symb.greek.upper.Pi")}"
                >$\Pi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Sigma"
                title="${_("kbd.symb.greek.upper.Sigma")}"
                >$\Sigma$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Upsilon"
                title="${_("kbd.symb.greek.upper.Upsilon")}"
                >$\Upsilon$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Phi"
                title="${_("kbd.symb.greek.upper.Phi")}"
                >$\Phi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Psi"
                title="${_("kbd.symb.greek.upper.Psi")}"
                >$\Psi$
        </button>
        <button
                class="btn btn-xs" data-inserts="\Omega"
                title="${_("kbd.symb.greek.upper.Omega")}"
                >$\Omega$
        </button>
    </div>
</div>
<!-- /Greek uppercase -->
</div>


<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_num">${_("kbd.num.title")}</button>
<!-- Numbers and fractions panel -->
<div id="kbd_num" class="kbd collapse">
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_num_num">${_("kbd.num.num.title")}</p>

    <div id="kbd_num_num" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\max(,)" data-back="2"
                title="${_("kbd.num.num.max")}"
                >max(a,b)
        </button>
        <button class="btn btn-xs"
                data-inserts="\min(,)" data-back="2"
                title="${_("kbd.num.num.min")}"
                >min(a,b)
        </button>
        <button class="btn btn-xs"
                data-inserts="\abs()" data-back="1"
                title="${_("kbd.num.num.abs")}"
                >abs()
        </button>
        <button class="btn btn-xs"
                data-inserts="\sign()" data-back="1"
                title="${_("kbd.num.num.sign")}"
                >sign()
        </button>
        <button class="btn btn-xs"
                data-inserts="\round()" data-back="1"
                title="${_("kbd.num.num.round")}"
                >round()
        </button>
        <button class="btn btn-xs"
                data-inserts="floor()" data-back="1"
                title="${_("kbd.num.num.floor")}"
                >floor()
        </button>
        <button class="btn btn-xs"
                data-inserts="\ceil()" data-back="1"
                title="${_("kbd.num.num.ceil")}"
                >ceil()
        </button>
        <button class="btn btn-xs"
                data-inserts="\isZero()" data-back="1"
                title="${_("kbd.num.num.isZero")}"
                >isZero()
        </button>
        <button class="btn btn-xs"
                data-inserts="\isOne()" data-back="1"
                title="${_("kbd.num.num.isOne")}"
                >isOne()
        </button>
        <button class="btn btn-xs"
                data-inserts="\isEven()" data-back="1"
                title="${_("kbd.num.num.isEven")}"
                >isEven()
        </button>
        <button class="btn btn-xs"
                data-inserts="\isNegative()" data-back="1"
                title="${_("kbd.num.num.isNegative")}"
                >isNegative()
        </button>
        <button class="btn btn-xs"
                data-inserts="\isInfinite()" data-back="1"
                title="${_("kbd.num.num.isInfinite")}"
                >isInfinite()
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[x,y];\randomNumber();" data-back="2"
                title="${_("kbd.num.num.random")}"
                >random(bits)
        </button>
    </div>
 </div>
 <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_num_int">${_("kbd.num.int.title")}</p>
    <div id="kbd_num_int" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\div(,)" data-back="2"
                title="${_("kbd.num.int.div")}"
                >div(a,b)
        </button>
        <button class="btn btn-xs"
                data-inserts="\rem(,)" data-back="2"
                title="${_("kbd.num.int.rem")}"
                >rem(a,b)
        </button>
        <button class="btn btn-xs"
                data-inserts="\divRem(,)" data-back="2"
                title="${_("kbd.num.int.divRem")}"
                >divRem(a,b)
        </button>        
        <button class="btn btn-xs"
                data-inserts="\mod(,)" data-back="2"
                title="${_("kbd.num.int.mod")}"
                >mod(a,m)
        </button> 
        <button class="btn btn-xs"
                data-inserts="\Mod(,)" data-back="2"
                title="${_("kbd.num.int.ModCentered")}"
                >Mod(a,m)
        </button> 
        <button class="btn btn-xs"
                data-inserts="\factor()" data-back="1"
                title="${_("kbd.num.int.factor")}"
                >factor(a)
        </button>        <button class="btn btn-xs"
                data-inserts="a!" data-back="1"
                title="${_("kbd.num.int.Factorial")}"
                >a!
        </button>
        <button class="btn btn-xs"
                data-inserts="\GCD(,)" data-back="2"
                title="${_("kbd.num.int.gcd")}"
                >GCD(a,b)
        </button>
        <button class="btn btn-xs"
                data-inserts="\extendedGCD(,)" data-back="2"
                title="${_("kbd.num.int.extgcd")}"
                >extendedGCD(a,b)
        </button>
      </div>
   </div>

   <div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_num_frac">${_("kbd.num.frac.title")}</p>

    <div id="kbd_num_frac" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\num()" data-back="1"
                title="${_("kbd.num.frac.num")}"
                >num(fr)
        </button>
        <button class="btn btn-xs"
                data-inserts="\denom()" data-back=1"
                title="${_("kbd.num.frac.denom")}"
                >denom(fr)
        </button>
        <button class="btn btn-xs"
                data-inserts="\cancel()" data-back=1"
                title="${_("kbd.num.frac.cancel")}"
                >cancel(fr)
        </button>
        <button class="btn btn-xs" 
                data-inserts="\quotientAndRemainder()" data-back="1"
                title="${_("kbd.num.frac.quotientAndRemainder")}"
                >quotientAndRemainder(fr)
        </button>
        <button class="btn btn-xs"
                data-inserts="\quotient()" data-back="1"
                title="${_("kbd.num.frac.quotient")}"
                >quotient(fr)
        </button>
        <button class="btn btn-xs"
                data-inserts="\remainder()" data-back="1"
                title="${_("kbd.num.frac.remainder")}"
                >remainder(fr)
        </button>
        <button class="btn btn-xs"
                data-inserts="\quotientAndRemainder(,)" data-back="2"
                title="${_("kbd.num.frac.quotientAndRemainder")}"
                >quotientAndRemainder(fr,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\quotient(,)" data-back="2"
                title="${_("kbd.num.frac.quotient")}"
                >quotient(fr,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\remainder(,)" data-back="2"
                title="${_("kbd.num.frac.remainder")}"
                >remainder(fr,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\properForm();" data-back="2"
                title="${_("kbd.num.frac.properForm")}"
                >properForm(fr)
        </button>
       </button>
      </div>
   </div>

   <div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_num_pol">${_("kbd.num.pol.title")}</p>
        <div id="kbd_num_pol" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[x,y];\randomPolynom(2, 2, 100, 5);" data-back="0"
                title="${_("kbd.num.pol.randomPolynom")}"
                >random(d_1,..,d_n,density,bits)
        </button>
        <button class="btn btn-xs"
                data-inserts="\GCD(,);" data-back="3"
                title="${_("kbd.num.pol.GCD")}"
                >GCD(f,g)
        </button>
        <button class="btn btn-xs"
                data-inserts="\LCM(,);" data-back="3"
                title="${_("kbd.num.pol.LCM")}"
                >LCM(f,g)
        </button>
        <button class="btn btn-xs"
                data-inserts="\leadingCoeff();" data-back="2"
                title="${_("kbd.num.pol.leadingCoeff")}"
                >leadingCoeff()
        </button>
        <button class="btn btn-xs"
                data-inserts="\value(,[]);" data-back="5"
                title="${_("kbd.num.pol.valuex")}"
                >value(f(x),[a])
        </button>
        <button class="btn btn-xs"
                data-inserts="\value(,[,]);" data-back="6"
                title="${_("kbd.num.pol.value")}"
                >value(f(x,y), [a, b])
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = Q[x];b = \solve(x^2 + 3x - 6 = 0);" data-back="1"
                title="${_("kbd.num.pol.rootsOfPolynomial")}"
                >rootsOfPolynomial()
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = R64[x];b = \solve(x^3+8);" data-back="1"
                title="${_("kbd.num.pol.realRootsOfPolynomial")}"
                >realRootsOfPolynomial()
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = C64[x];b = \solve(x^3+8);" data-back="1"
                title="${_("kbd.num.pol.complexRootsOfPolynomial")}"
                >complexRootsOfPolynomial()
        </button>
         <button class="btn btn-xs"
                data-inserts="SPACE = R64[x, y]; \solveNAE(x^2 + y^2 - 4, y - x^2);" data-back="1"
                title="${_("kbd.num.pol.solveNAE")}"
                >solveNAE(f,g,...,h)
        </button>
         <button class="btn btn-xs"
                data-inserts="\degrees();" data-back="2"
                title="${_("kbd.num.pol.degrees")}"
                >\degrees(f)
        </button>
         <button class="btn btn-xs"
                data-inserts="\degree();" data-back="2"
                title="${_("kbd.num.pol.degree")}"
                >\degree(f)
        </button>
        <button class="btn btn-xs"
               data-inserts="SPACE = Z[x, y, z]; \groebner(x^4y^3 + 2xy^2 + 3x + 1, x^3y^2 + x^2, x^4y + z^2 + xy^4 + 3);" data-back="0"
               title="${_("kbd.num.pol.gbasis")}"
               >groebner(f,..,g)
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = Q[x, y, z];\reduceByGB(5y^2 + 3x^2 + z^2, [y + x, 5z^2 + 5z]);" data-back="0"
                title="${_("kbd.num.pol.reduceByGB")}"
                >reduceByGB(f,[p,..,q])
        </button>
        <button class="btn btn-xs"
                data-inserts="\PRS(,)" data-back="2"
                title="${_("kbd.num.pol.PRS")}"
                >PRS(a,b)
        </button>
         <button class="btn btn-xs"
                data-inserts="\GCDNumPolCoeffs()" data-back="1"
                title="${_("kbd.num.pol.GCDNumCf")}"
                >GCDNumCf(p)
        </button>
         <button class="btn btn-xs"
                data-inserts="\GCDHPolCoeffs()" data-back="1"
                title="${_("kbd.num.pol.GCDHCf")}"
                >GCDHCf(p,z)
        </button>
         <button class="btn btn-xs"
                data-inserts="\quotientAndRemainder(,,)" data-back="3"
                title="${_("kbd.num.pol.quotientAndRemainderx")}"
                >quotientAndRemainder(a,b,x)
        </button>
         <button class="btn btn-xs"
                data-inserts="\quotient(,,)" data-back="3"
                title="${_("kbd.num.pol.quotientx")}"
                >quotient(a,b,x)
        </button>
         <button class="btn btn-xs"
                data-inserts="\remainder(,,)" data-back="3"
                title="${_("kbd.num.pol.remainderx")}"
                >remainder(a,b,x)
        </button>
         <button class="btn btn-xs"
                data-inserts="SPACE=Q[x,y]; a=3xy+1; b=4y+x;V=\extendedGCD(a,b); v=\elementOf(V); c= a v_{2}+b v_{3}; \print(c, v_{1},v_{2}, v_{3});" data-back="2"
                title="${_("kbd.num.pol.extendedGCD")}"
                >extendedGCD(a,b)
        </button>
         <button class="btn btn-xs"
                data-inserts="\toVectorDence();" data-back="2"
                title="${_("kbd.num.pol.toVectorDence")}"
                >toVectorDence(p)
        </button>
         <button class="btn btn-xs"
                data-inserts="\toVectorSparce();" data-back="2"
                title="${_("kbd.num.pol.toVectorSparce")}"
                >toVectorSparce(p)
        </button>
         <button class="btn btn-xs"
                data-inserts="\vectorToPolynom();" data-back="2"
                title="${_("kbd.num.pol.vectorToPolynom")}"
                >vectorToPolynom([])
        </button>
    </div>
 </div>
<!-- /Numbers and Polynomials -->
</div>
<!-- Matrix panel -->


<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_mat">${_("kbd.mat.title")}</button>
 <div id="kbd_mat" class="kbd collapse">
  <div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_mat_matr">${_("kbd.mat.matr.title")}</p>

    <div id="kbd_mat_matr" class="collapse">
        <button class="btn btn-xs"
                data-inserts="M=[[1,2],[3,1]]; b=[1,1]; \solve(M,b); " data-back="0"
                title="${_("kbd.mat.matr.solveLAE")}"
                >solveLAE
        </button>
        <button class="btn btn-xs"
                data-inserts="^{T};" data-back="0"
                title="${_("kbd.mat.matr.transpose")}"
                >transpose
        </button>
        <button class="btn btn-xs"
                data-inserts="^{-1};" data-back="0"
                title="${_("kbd.mat.matr.inverse")}"
                >inverse
        </button>
        <button class="btn btn-xs"
                data-inserts="\adjoint();" data-back="0"
                title="${_("kbd.mat.matr.adjoint")}"
                >adjoint
        </button>
        <button class="btn btn-xs"
                data-inserts="\det();" data-back="0"
                title="${_("kbd.mat.matr.det")}"
                >det
        </button>
        <button class="btn btn-xs"
                data-inserts="\detL(n,[p(x,y),x,q(x,y),y]);" data-back="0"
                title="${_("kbd.mat.matr.detL")}"
                >detL
        </button>
        <button class="btn btn-xs"
                data-inserts="^{\ast};" data-back="0"
                title="${_("kbd.mat.matr.conjugate")}"
                >conjugate
        </button>
        <button class="btn btn-xs"
                data-inserts="^{+};" data-back="0"
                title="${_("kbd.mat.matr.genInverse")}"
                >genInverse
        </button>
        <button class="btn btn-xs"
                data-inserts="\toEchelonForm();" data-back="0"
                title="${_("kbd.mat.matr.toEchelonForm")}"
                >toEchelonForm
        </button>
        <button class="btn btn-xs"
                data-inserts="\kernel();" data-back="0"
                title="${_("kbd.mat.matr.kernel")}"
                >kernel
        </button>
        <button class="btn btn-xs"
                data-inserts="\charPolynom();" data-back="0"
                title="${_("kbd.mat.matr.charPolynom")}"
                >charPolynom
        </button>
        <button class="btn btn-xs"
                data-inserts="\LDU();" data-back="0"
                title="${_("kbd.mat.matr.LDU")}"
                >LDU
        </button>
        <button class="btn btn-xs"
                data-inserts="\BruhatDecomposition();" data-back="0"
                title="${_("kbd.mat.matr.BruhatDecomposition")}"
                >BruhatDecomposition
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[x,y];\randomMatrix(2, 2, 100, 5);" data-back="0"
                title="${_("kbd.mat.matr.randomMatrix")}"
                >random(n,m,density,bits)
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[x,y,z];\randomMatrix(2, 2, 100, 1,1,1,50, 5);" data-back="0"
                title="${_("kbd.mat.matr.randomMatrixP")}"
                >random(n,m,dens,d1,.,dk,dens,bits)
        </button>
        <button class="btn btn-xs"
                data-inserts="\submatrix(A,1,1,1,1);" data-back="10"
                title="${_("kbd.mat.matr.submatrix")}"
                >submatrix(M,r1,Nr,c1,Nc)
        </button>
    </div>
  </div>
  <div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_mat_integ">${_("kbd.mat.integ.title")}</p>
    <div id="kbd_mat_integ" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[];  A=[[5,2],[3,2]]; \LDUm(A);" data-back="2"
                title="${_("kbd.mat.integ.LDUm")}"
                >LDUm
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[];\PlduQwk();" data-back="2"
                title="${_("kbd.mat.integ.PLDUQWDK")}"
                >PlduQwk
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[];\LDUWK();" data-back="2"
                title="${_("kbd.mat.integ.LDUWDK")}"
                >LDUWK
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Z[]; \WDK();" data-back="2"
                title="${_("kbd.mat.integ.WDK")}"
                >WDK
        </button>
    </div>
</div> 
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_mat_lprog">${_("kbd.mat.lprog.title")}</p>
    <div id="kbd_mat_lprog" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=Q[];A=[[1,3],[2,1]];b=[3,2];c=[8,6];\SimplexMax(A,b,c);" data-back="0"
                title="${_("kbd.mat.lprog.SimplexMax")}"
                >SimplexMax
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Q[];A=[[3,4],[2,1]];b=[3,1];c=[-2,-1];\SimplexMin(A,b,c);" data-back="2"
                title="${_("kbd.mat.lprog.SimplexMin")}"
                >SimplexMin
        </button>
    </div>
</div> 

<!-- /Matrix -->
</div>

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_functions">${_("kbd.func.title")}</button>
<!-- Functions panel -->
<div id="kbd_functions" class="kbd collapse">
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_functions_one_var">${_("kbd.func.onearg.title")}</p>

    <div id="kbd_functions_one_var" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\ln()" data-back="1"
                title="${_("kbd.func.onearg.ln")}"
                >ln
        </button>
        <button class="btn btn-xs"
                data-inserts="\lg()" data-back="1"
                title="${_("kbd.func.onearg.lg")}"
                >lg
        </button>
        <button class="btn btn-xs"
                data-inserts="\sin()" data-back="1"
                title="${_("kbd.func.onearg.sin")}"
                >sin
        </button>
        <button class="btn btn-xs"
                data-inserts="\cos()" data-back="1"
                title="${_("kbd.func.onearg.cos")}"
                >cos
        </button>
        <button class="btn btn-xs"
                data-inserts="\tg()" data-back="1"
                title="${_("kbd.func.onearg.tg")}"
                >tg
        </button>
        <button class="btn btn-xs"
                data-inserts="\ctg()" data-back="1"
                title="${_("kbd.func.onearg.ctg")}"
                >ctg
        </button>
        <button class="btn btn-xs"
                data-inserts="\arcsin()" data-back="1"
                title="${_("kbd.func.onearg.arcsin")}"
                >arcsin
        </button>
        <button class="btn btn-xs"
                data-inserts="\arccos()" data-back="1"
                title="${_("kbd.func.onearg.arccos")}"
                >arccos
        </button>
        <button class="btn btn-xs"
                data-inserts="\arctg()" data-back="1"
                title="${_("kbd.func.onearg.arctg")}"
                >arctg
        </button>
        <button class="btn btn-xs"
                data-inserts="\arcctg()" data-back="1"
                title="${_("kbd.func.onearg.arcctg")}"
                >arcctg
        </button>
        <button class="btn btn-xs"
                data-inserts="\sh()" data-back="1"
                title="${_("kbd.func.onearg.sh")}"
                >sh
        </button>
        <button class="btn btn-xs"
                data-inserts="\ch()" data-back="1"
                title="${_("kbd.func.onearg.ch")}"
                >ch
        </button>
        <button class="btn btn-xs"
                data-inserts="\th()" data-back="1"
                title="${_("kbd.func.onearg.th")}"
                >th
        </button>
        <button class="btn btn-xs"
                data-inserts="\cth()" data-back="1"
                title="${_("kbd.func.onearg.cth")}"
                >cth
        </button>
        <button class="btn btn-xs"
                data-inserts="\arcsh()" data-back="1"
                title="${_("kbd.func.onearg.arcsh")}"
                >arcsh
        </button>
        <button class="btn btn-xs"
                data-inserts="\arcch()" data-back="1"
                title="${_("kbd.func.onearg.arcch")}"
                >arcch
        </button>
        <button class="btn btn-xs"
                data-inserts="\arcth()" data-back="1"
                title="${_("kbd.func.onearg.arcth")}"
                >arcth
        </button>
        <button class="btn btn-xs"
                data-inserts="\arccth()" data-back="1"
                title="${_("kbd.func.onearg.arccth")}"
                >arccth
        </button>
        <button class="btn btn-xs"
                data-inserts="\exp()" data-back="1"
                title="${_("kbd.func.onearg.exp")}"
                >exp
        </button>
        <button class="btn btn-xs"
                data-inserts="\sqrt()" data-back="1"
                title="${_("kbd.func.onearg.sqrt")}"
                >$\sqrt{}$
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_many">${_("kbd.func.many.title")}</p>

    <div id="kbd_func_many" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\log(a, x);" data-back="0"
                title="${_("kbd.func.many.log")}"
                >$\log_a(x)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\rootOf(a, n);" data-back="0"
                title="${_("kbd.func.many.sqrt")}"
                >$\sqrt[n]{a}$
        </button>
        <button class="btn btn-xs"
                data-inserts="\int(f) d x;" data-back="0"
                title="${_("kbd.func.many.int")}"
                >$\int \! f \ \mathrm{d}x$
        </button>
        <button class="btn btn-xs"
                data-inserts="\D(f);" data-back="0"
                title="${_("kbd.func.many.D")}"
                >$D(f)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\D(f, x);" data-back="0"
                title="${_("kbd.func.many.Dwithvar")}"
                >$D_{x}(f)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\D(f, x^n);" data-back="0"
                title="${_("kbd.func.many.DwithvarN")}"
                >$D_{x}^{(n)}(f)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\lim_{x \to a}(f);" data-back="0"
                title="${_("kbd.func.many.lim")}"
                >$\lim_{x \to a}(f)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\binom(n, k);" data-back="0"
                title="${_("kbd.func.many.binom")}"
                >$\binom n k $
        </button>
        <button class="btn btn-xs"
                data-inserts="\abs()" data-back="1"
                title="${_("kbd.func.many.abs")}"
                >abs
        </button>
        <button class="btn btn-xs"
                data-inserts="\sign()" data-back="1"
                title="${_("kbd.func.many.sign")}"
                >sign
        </button>
        <button class="btn btn-xs"
                data-inserts="a!" data-back="1"
                title="${_("kbd.func.many.fact")}"
                >a!
        </button>
        <button class="btn btn-xs"
                data-inserts="\Gamma()" data-back="1"
                title="${_("kbd.func.many.Gamma")}"
                >$\Gamma(x)$
        </button>
        <button class="btn btn-xs"
                data-inserts="\Beta(x, y);" data-back="0"
                title="${_("kbd.func.many.Beta")}"
                >&Beta;(x,y)
        </button>
        <button class="btn btn-xs"
                data-inserts="\BesselJ(n,x);" data-back="0"
                title="${_("kbd.func.many.BESSELJ")}"
                >BessJ(n,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\BesselY(n,x);" data-back="0"
                title="${_("kbd.func.many.BESSELY")}"
                >BessY(n,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\LegendreP(n,x);" data-back="0"
                title="${_("kbd.func.many.P2")}"
                >LegP(n,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\LegendreP(n,m,x);" data-back="0"
                title="${_("kbd.func.many.P3")}"
                >LegP(n,m,x)
        </button>
        <button class="btn btn-xs"
                data-inserts="\sphericalHarmonic(n,m,\theta,\phi);" data-back="0"
                title="${_("kbd.func.many.Y")}"
                >sphHarm
        </button>
        <button class="btn btn-xs"
                data-inserts="\sphericalHarmonicR(n,m,r,\theta,\phi);" data-back="0"
                title="${_("kbd.func.many.Yr")}"
                >sphHarmR
        </button>
        <button class="btn btn-xs"
                data-inserts="\sphericalHarmonicCart(n,m,x,y,z);" data-back="0"
                title="${_("kbd.func.many.YCart")}"
                >sphHarmCart
        </button>
        <button class="btn btn-xs"
                data-inserts="\sphericalHarmonicRCart(n,m,x,y,z);" data-back="0"
                title="${_("kbd.func.many.YrCart")}"
                >sphHarmRCart
        </button>
    </div>
</div>
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_nonlinear">${_("kbd.func.nonlinear.title")}</p>
    <div id="kbd_func_nonlinear" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=Q[x]; \solve(x^2+5x+p=0);" data-back="2"
                title="${_("kbd.func.nonlinear.solveEquation")}"
                >solveEquation
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=Q[x,y];\solve([x+y=6,3x-2y=5]);" data-back="2"
                title="${_("kbd.func.nonlinear.solveSystemOfEquations")}"
                >solveLinSyst
        </button>
        <button class="btn btn-xs"
                data-inserts="\solve([a+b+c=6,3c*a-2b-7d=5],[a,b]);" data-back="9"
                title="${_("kbd.func.nonlinear.solveSystemWithOtherVariables")}"
                >solveLSystWithOtherVars
        </button>
        <button class="btn btn-xs"
                data-inserts="A=[[1,2],[4,5]]; b=[3,4];\solve(A,b);" data-back="29"
                title="${_("kbd.func.nonlinear.solveSystemInMatrixForm")}"
                >solveLSystInMatrixForm
        </button>   
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[x,y];\solveNAE(x^2+y^2-4,y-x^2);" data-back="1"
                title="${_("kbd.func.nonlinear.solveNAE")}"
                >solveNAE(f,g,...,h)
        </button>     
        <button class="btn btn-xs"
                data-inserts="SPACE = R64[x];b = \solveTrig(\sin(x)=0.5);" data-back="0"
                title="${_("kbd.func.nonlinear.solveTrig")}"
                >solveTrigonometricEquation
        </button>
        <button class="btn btn-xs"
                data-inserts="\solve((x+1)^2(x-3)(x+5) > 0);" data-back="0"
                title="${_("kbd.func.nonlinear.solveIn")}"
                >solveInequality
        </button>
        <button class="btn btn-xs"
                data-inserts="\solve(x^2+4x-5 > 0, x^2-2x-8 < 0);" data-back="0"
                title="${_("kbd.func.nonlinear.solveSI")}"
                >solveSystemOfInequalities
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[x,y]; f=x^2-7y; \value(f,[1,2]);" data-back="0"
                title="${_("kbd.func.nonlinear.value")}"
                >value
        </button>
    </div>
</div>
<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_diff">${_("kbd.func.diff.title")}</p>
    <div id="kbd_func_diff" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[t];\newline  g=\systLDE(\d(y, t, 3)+3\d(y, t, 2)+3\d(y, t)+y=1);\newline f=\initCond(\d(y, t, 0, 0)=0, \d(y, t, 0, 1)=0,\d(y, t, 0, 2)=0);\newline h=\solveLDE(g, f); "
                data-back="0"
                title="${_("kbd.func.diff.solveLinearDE")}"
                >solveLinDE
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[t];\newline g=\systLDE(\d(x, t)-y+z=0, -x-y+\d(y, t)=0, -x-z+\d(z, t)=0);\newline f= \initCond(\d(x, t, 0, 0)=1, \d(y, t, 0, 0)=2, \d(z, t, 0, 0)=3);\newline h= \solveLDE(g, f);"
                data-back="0"
                title="${_("kbd.func.diff.solveSystemOfLinearDE")}"
                >solveSystemOfLinDEs
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_prob">${_("kbd.func.prob.title")}</p>

    <div id="kbd_func_prob" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[x];DRQ=[[10,20,33,22],[0.1,0.5,0.2, 0.2]];" data-back="0"
                title="${_("kbd.func.prob.discreteRandomQuantity")}"
                >discreteRandomQuantity
        </button>
        <button class="btn btn-xs"
                data-inserts="\mathExpectation(DRQ);" data-back="0"
                title="${_("kbd.func.prob.Expectation")}"
                >mathExpectation
        </button>
        <button class="btn btn-xs"
                data-inserts="\dispersion(DRQ);" data-back="0"
                title="${_("kbd.func.prob.dispersion")}"
                >dispersion
        </button>
        <button class="btn btn-xs"
                data-inserts="\meanSquareDeviation(DRQ);" data-back="0"
                title="${_("kbd.func.prob.meanSquareDeviation")}"
                >meanSquareDeviation
        </button>
        <button class="btn btn-xs"
                data-inserts="\addQU(DRQ1,DRQM2);" data-back="0"
                title="${_("kbd.func.prob.addQU")}"
                >addQU
        </button>
        <button class="btn btn-xs"
                data-inserts="\multiplyQU(DRQ1, DRQ2);" data-back="0"
                title="${_("kbd.func.prob.multiplyQU")}"
                >multiplyQU
        </button>
        <button class="btn btn-xs"
                data-inserts="\covariance(DRQ1, DRQ2);" data-back="0"
                title="${_("kbd.func.prob.covariance")}"
                >covariance
        </button>
        <button class="btn btn-xs"
                data-inserts="\correlation(DRQ1, DRQ2);" data-back="0"
                title="${_("kbd.func.prob.correlation")}"
                >correlation
        </button>
        <button class="btn btn-xs"
                data-inserts="\plotPolygonDistribution(DRQ);" data-back="0"
                title="${_("kbd.func.prob.plotPolygonDistribution")}"
                >plotPolygonDistribution
        </button>
        <button class="btn btn-xs"
                data-inserts="\plotDistributionFunction(DRQ);" data-back="0"
                title="${_("kbd.func.prob.plotDistributionFunction")}"
                >plotDistributionFunction
        </button>
        <button class="btn btn-xs"
                data-inserts="\simplifyQU(DRQ);" data-back="0"
                title="${_("kbd.func.prob.simplifyQU")}"
                >simplifyQU
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_stat">${_("kbd.func.stat.title")}</p>

    <div id="kbd_func_stat" class="collapse">
        <button class="btn btn-xs"
                data-inserts="SPACE=R64[x];S=[10,20,33,22];" data-back="0"
                title="${_("kbd.func.stat.sample")}"
                >sample
        </button>
        <button class="btn btn-xs"
                data-inserts="\sampleMean(S);" data-back="0"
                title="${_("kbd.func.stat.sampleMean")}"
                >sampleMean
        </button>
        <button class="btn btn-xs"
                data-inserts="\sampleDispersion(S);" data-back="0"
                title="${_("kbd.func.stat.sampleDispersion")}"
                >sampleDispersion
        </button>
        <button class="btn btn-xs"
                data-inserts="\covarianceCoefficient(S1, S2);" data-back="0"
                title="${_("kbd.func.stat.covarianceCoefficient")}"
                >covarianceCoefficient
        </button>
        <button class="btn btn-xs"
                data-inserts="\correlationCoefficient(S1, S2);" data-back="0"
                title="${_("kbd.func.stat.correlationCoefficient")}"
                >correlationCoefficient
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_graph">${_("kbd.func.graph.title")}</p>

    <div id="kbd_func_graph" class="collapse">
        <button class="btn btn-xs"
                data-inserts="M=[[0,1,1,0,1,0],[1,0,0,1,1,0],[1,0,0,0,1,1],[0,1,0,0,0,0],
                [1,1,1,0,0,1],[0,0,1,0,1,0]];\newline   \plotGraph(M);" data-back="0"
                title="${_("kbd.func.graph.plotGraphMatrix")}"
                >plotGraphMatrix
        </button>
        <button class="btn btn-xs"
                data-inserts="M=[[0,1,1,0,1,0],[1,0,0,1,1,0],[1,0,0,0,1,1],[0,1,0,0,0,0],
                [1,1,1,0,0,1],[0,0,1,0,1,0]];\newline P=[[3,2,4,1,3,5],[3,2,2,1,1,1]];\newline \plotGraph(M,P);" data-back="0"
                title="${_("kbd.func.graph.plotGraphAdMatrixCoord")}"
                >plotGraphAdMatrixCoord
        </button>
        <button class="btn btn-xs"
                data-inserts="\plotGraph(5);" data-back="0"
                title="${_("kbd.func.graph.plotCompleteGraph")}"
                >plotCompleteGraph
        </button>
        <button class="btn btn-xs"
                data-inserts="\searchLeastDistances()" data-back="1"
                title="${_("kbd.func.graph.searchLeastDistances")}"
                >searchLeastDistances
        </button>
        <button class="btn btn-xs"
                data-inserts="\findTheShortestPath()" data-back="1"
                title="${_("kbd.func.graph.findTheShortestPath")}"
                >findTheShortestPath
        </button>

    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_tropfield">${_("kbd.func.tropfield.title")}</p>
    <div id="kbd_func_tropfield" class="collapse">
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MinPlus[];" data-back="2"
                title="${_("kbd.func.tropfield.R64MinPlus")}"
                >R64min+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMinPlus[];" data-back="2"
                title=""
                >Rmin+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MinMult[];" data-back="2"
                title=""
                >R64min$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMinMult[];" data-back="2"
                title=""
                >Rmin$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MaxPlus[];" data-back="2"
                title=""
                >R64max+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMaxPlus[];" data-back="2"
                title=""
                >Rmax+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MaxMult[];" data-back="2"
                title=""
                >R64max$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMaxMult[];" data-back="2"
                title=""
                >Rmax$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MinMax[];" data-back="2"
                title=""
                >R64min max 
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMinMax[];" data-back="2"
                title=""
                >Rmin max
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = R64MaxMin[];" data-back="2"
                title=""
                >R64max min 
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = RMaxMin[];" data-back="2"
                title=""
                >Rmax min
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMinPlus[];" data-back="2"
                title=""
                >Zmin+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMinMult[];" data-back="2"
                title=""
                >Zmin$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMaxPlus[];" data-back="2"
                title=""
                >Zmax+
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMaxMult[];" data-back="2"
                title=""
                >Zmax$\times$
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMinMax[];" data-back="2"
                title=""
                >Zmin max
        </button>
        <button
                class="btn btn-xs" data-inserts="SPACE = ZMaxMin[];" data-back="2"
                title=""
                >Zmax min
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = ZMinPlus[x, y]; A = [[1, 1],[2, 0]];\newline b = [10, 7];\newline \solveLAETropic(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.solveLAETropic")}"
                >Ax=b
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = ZMinPlus[x, y];  A = [[1, 1],[2, 0]];\newline b = [10, 7];\newline \solveLAITropic(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.solveLAITropic")}"
                >Ax ≤ b
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = R64MaxPlus[x, y];  A = [[0 , -2 ], [-\infty, 0 ]];  b = [ 1 , -\infty ];\newline \BellmanEquation(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanEquation")}"
                >Ax+b=x
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = R64MaxPlus[x, y];  A = [[0 , -2 ], [-\infty, 0 ]];  b = [ 1 , -\infty ];\newline \BellmanInequality(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanInequality")}"
                >Ax+b≤x
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_algebraic">${_("kbd.func.algebraic.title")}</p>

    <div id="kbd_func_algebraic" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\factor();" data-back="2"
                title="${_("kbd.func.algebraic.factor")}"
                >factor
        </button>
        <button class="btn btn-xs"
                data-inserts="\expand();" data-back="2"
                title="${_("kbd.func.algebraic.expand")}"
                >expand
        </button>
    </div>
</div>

<div class="well well-small">
    <p class="label label-default btn-block collapsed" data-toggle="collapse"
       data-target="#kbd_func_transcend">${_("kbd.func.transcend.title")}</p>

    <div id="kbd_func_transcend" class="collapse">
        <button class="btn btn-xs"
                data-inserts="\Factor();" data-back="2"
                title="${_("kbd.func.transcend.Factor")}"
                >Factor
        </button>
        <button class="btn btn-xs"
                data-inserts="\Expand();" data-back="2"
                title="${_("kbd.func.transcend.Expand")}"
                >Expand
        </button>
    </div>
  </div>
</div>

 

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_plots">${_("kbd.plot.title")}</button>
<!-- Plots panel -->
<div id="kbd_plots" class="kbd collapse">
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_plot_plot2d">${_("kbd.plot.2d.title")}</p>

        <div id="kbd_plot_plot2d" class="collapse in">
            <button class="btn btn-xs" data-inserts="\set2D(-5,5,-4,6); f=3\arctg(x+1); \plot([f,-x+5, 3x+5]);"
                    data-back="0"
                    title="${_("kbd.plot.2d.plot")}"
                    >plot
            </button>
            <button class="btn btn-xs" data-inserts="\set2D(-20, 20, -20, 20);\newline g = x\sin(x); k = x\cos(x);\newline f = \paramPlot([g, k], [0, 5\pi]);"
                    data-back="0"
                    title="${_("kbd.plot.2d.paramPlot")}"
                    >paramPlot
            </button>
            <button class="btn btn-xs"
                    data-inserts="\set2D( 0, 10,  -5, 30);\newline \tablePlot([[0, 1, 2, 3, 4, 5],[0, 1, 4, 9, 16, 25],[0, -1, -2, -3, -4, -5],[0, 4, 8, 12, 16, 20]]);"
                    data-back="0"
                    title="${_("kbd.plot.2d.tablePlot")}"
                    >tablePlot
            </button>
            <button class="btn btn-xs"
                    data-inserts="\set2D(-5, 5, -5, 5);\newline f1 = \plot(\tg(x));\newline f2 = \tablePlot([[0, 1, 4, 9, 16, 25], [0, 1, 2, 3, 4, 5]]);\newline f3 = \paramPlot([\sin(x), \cos(x)], [-10, 10]);\newline f4=\tablePlot([[0, 1, 4, 9, 16, 25], [0, -1, -2, -3, -4, -5]]);\newline \showPlots([f1, f2, f3, f4]);"
                    data-back="0"
                    title="${_("kbd.plot.2d.showPlots")}"
                    >showPlots
            </button>
            <button class="btn btn-xs"
                    data-inserts=" SPACE=R64[x];\set2D(0, 5, -5, 10);\newline A=[[0, 1, 2, 3,  4, 5],[3, 0, 4, 10, 5, 10]];\newline  t=\table(A);\newline p=\approximation(t,4);\newline P=\plot(p );\newline T=\tablePlot(t);\showPlots([P,T]);\print(p);"
                    data-back="0"
                    title="${_("kbd.plot.2d.approx")}"
                    >approximation
            </button>
            <button class="btn btn-xs" data-inserts="=\tableFromFile('');" data-back="3"
                    title="${_("kbd.plot.2d.tableFromFile")}"
                    >tableFromFile
            </button>
            <button class="btn btn-xs" data-inserts="=\table();" data-back="2"
                    title="${_("kbd.plot.2d.table")}"
                    >table
            </button>
        </div>
    </div>
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_plot_plot3d">${_("kbd.plot.3d.title")}</p>
        <div id="kbd_plot_plot3d" class="collapse in">
            <button class="btn btn-xs" data-inserts="\clean(); SPACE = R64[x, y, z];f = x^2 / 20 + y^2 / 20;\newline pl=\plot3d([f], [-20, 20, -20, 20]);"
                    data-back="0"
                    title="${_("kbd.plot.3d.plot")}"
                    >plot3d
            </button>
            <button class="btn btn-xs" data-inserts="\clean(); SPACE = R64[x, y, z];f = -x^2+2y^2+3z^2-25; iPl=\implicitPlot3d( f, -10, 10, -10, 10, -10, 10);"
                    data-back="0"
                    title="${_("kbd.plot.3d.implicitPlot1")}"
                    >implicitPlot3d_1
            </button>
            <button class="btn btn-xs" data-inserts="\clean(); SPACE = R64[x, y, z];f = (x^2+y^2+z^2)^2-80xyz; iPl=\implicitPlot3d( f, -10, 10, -10, 10, -10, 10);"
                    data-back="0"
                    title="${_("kbd.plot.3d.implicitPlot2")}"
                    >implicitPlot3d_2
            </button>
            <button class="btn btn-xs" data-inserts="\clean(); SPACE = R64[x, y, z];f = (x^2+y^2)/20; ePl=\explicitPlot3d( f, -10, 10, -10, 10, -10, 10, 40);"
                    data-back="0"
                    title="${_("kbd.plot.3d.explicitPlot1")}"
                    >explicitPlot3d
            </button>
        </div>
    </div>
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_plot_parm3d">${_("kbd.plot.parm3d.title")}</p>
        <div id="kbd_plot_parm3d" class="collapse in">
          <button class="btn btn-xs" data-inserts="\clean(); SPACE = R64[u, v]; X=\cos(u)*(3+\cos(v)); Y=\sin(u)*(3+\cos(v)); Z=\sin(v);pPl=\parametricPlot3d(X, Y, Z, 0, 7, 0, 7, 64);"
                    data-back="0"
                    title="${_("kbd.plot.parm3d.Torus")}"
                    >Torus
            </button>
            <button class="btn btn-xs" data-inserts="\clean();  SPACE = R64[u, v]; X=\cos(u)*(\cos(v)+2); Y=\sin(u) * (\cos(v)+2); Z=\sin(v)+u/2+1; pPl=\parametricPlot3d(X,Y,Z, -6.3, 6.3, -3.15, 3.15, 64);"
                    data-back="0"
                    title="${_("kbd.plot.parm3d.Spiral")}"
                    >Spiral
            </button>
            <button class="btn btn-xs" data-inserts="\clean();  SPACE = R64[u, v]; X = \cos(u) * \sin(v) * 2.5; Y = \sin(u) * \sin(v) * 2.5; Z = \cos(v) + \ln(\tg(v / 2)) + u / 6; pPl=\parametricPlot3d(X, Y, Z, 0, 12.56, 0.001, 2, 64);"
                    data-back="0"
                    title="${_("kbd.plot.parm3d.Dini")}"
                    >Dini's surface
            </button>
        </div>
    </div>
</div>

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_files">${_("kbd.files.title")}</button>
<!-- My files panel -->
<div id="kbd_files" class="kbd collapse">
    <div class="well well-small">
        <form id="import-form" action="../api/files"
              method="POST"
              enctype="multipart/form-data"
              accept-charset="UTF-8">
            <fieldset>
          <span class="btn btn-default btn-block fileinput-button">
            <i class="glyphicon glyphicon-upload"></i><span>${_("kbd.files.importtxt")}</span>
            <input type="file" name="file" id="file-import">
            <input type="hidden" name="import_txt">
          </span>
            </fieldset>
        </form>
        <button id="export_txt" class="btn btn-sm"
                title="${_("kbd.files.savetxt_tooltip")}"
                ><i class="glyphicon glyphicon-download-alt"></i> ${_("kbd.files.savetxt")}</button>
        <form id="export_settings" class="form-inline" role="form">
            <input id="export_format" name="format" type="hidden" value="pdf">
            <input name="filename" type="hidden" value="">
            <input id="pdf_page_width" name="pdf_page_width" type="text" class="input-sm form-control" value="21">
            x
            <input id="pdf_page_height" name="pdf_page_height" type="text" class="input-sm form-control" value="29.7">
            <button id="export_pdf" class="btn btn-sm"
                    title="${_("kbd.files.savepdf_tooltip")}"
                    ><i class="glyphicon glyphicon-download-alt"></i> ${_("kbd.files.savepdf")}</button>
        </form>
        <form id="upload-form" action="../api/files"
              method="POST"
              enctype="multipart/form-data"
              accept-charset="UTF-8">
            <fieldset>
          <span class="btn btn-default btn-block fileinput-button">
            <i class="glyphicon glyphicon-upload"></i><span>${_("kbd.files.select")}</span>
            <input type="file" name="file" id="file">
          </span>
            </fieldset>
        </form>
        <div id="filelist-container" class="filelist-container well well-small table-responsive">
        </div>
        <button class="btn btn-xs" data-inserts="\fromFile(fileName)" data-back="2"
                title="${_("kbd.files.fromFile")}"
                >fromFile
        </button>
        <button class="btn btn-xs" data-inserts="\toFile(a,fileName)" data-back="5"
                title="${_("kbd.files.toFile")}"
                >toFile
        </button>
    </div>
</div>
<!-- /kbd_files -->

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_cluster">${_("kbd.cluster.title")}</button>
<!-- Cluster panel -->
<div id="kbd_cluster" class="kbd collapse">
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_cluster_func">${_("kbd.cluster.func.title")}</p>

        <div id="kbd_cluster_func" class="collapse in">
            <button class="btn btn-xs" data-inserts="\getStatus();" data-back="2"
                    title="${_("kbd.cluster.func.getStatus")}"
                    >getStatus
            </button>
            <button class="btn btn-xs" data-inserts="\getCalcResult();" data-back="2"
                       title="${_("kbd.cluster.func.getCalcResult")}"
                    >getCalcResult
            </button>
            <button class="btn btn-xs" data-inserts="\matMultPar1x8(A, B);" data-back="5"
                    title="${_("kbd.cluster.func.matMultPar1x8")}"
                    >matrixMult:1x8
            </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = ZMinPlus[x, y]; A = [[1, 1],[2, 0]];\newline \BellmanEquationPar(A);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanEquation")}"
                >Tropical: Ax=x
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = ZMinPlus[x, y];  A = [[1, 1],[2, 0]]; \newline \BellmanInequalityPar(A);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanInequality")}"
                >Tropical: Ax ≤ x
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = R64MaxPlus[x, y];  A = [[0, -2], [-\infty, 0]];  b = [1, -\infty];\newline \BellmanEquationPar(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanEquation")}"
                >Tropical: Ax+b=x
        </button>
        <button class="btn btn-xs"
                data-inserts="SPACE = R64MaxPlus[x, y];  A = [[0, -2], [-\infty, 0]];  b = [1, -\infty];\newline \BellmanInequalityPar(A, b);"
                data-back="0"
                title="${_("kbd.func.tropfield.BellmanInequality")}"
                >Tropical: Ax+b≤x
        </button>
        </div>
    </div>
    <div class="well well-small">
        <p class="label label-default btn-block collapsed" data-toggle="collapse"
           data-target="#kbd_cluster_const">${_("kbd.cluster.const.title")}</p>

        <div id="kbd_cluster_const" class="collapse in">
            <button class="btn btn-xs" data-inserts="\TOTALNODES = ;" data-back="1"
                    title="${_("kbd.cluster.const.TOTALNODES")}"
                    >TOTALNODES
            </button>
            <button class="btn btn-xs" data-inserts="\PROCPERNODE = ;" data-back="1"
                    title="${_("kbd.cluster.const.PROCPERNODE")}"
                    >PROCPERNODE
            </button>
            <button class="btn btn-xs" data-inserts="\CLUSTERTIME = ;" data-back="1"
                    title="${_("kbd.cluster.const.CLUSTERTIME")}"
                    >CLUSTERTIME
            </button>
            <button class="btn btn-xs" data-inserts="\MAXCLUSTERMEMORY = ;" data-back="1"
                    title="${_("kbd.cluster.const.MAXCLUSTERMEMORY")}"
                    >MAXCLUSTERMEMORY
            </button>
        </div>
    </div>
</div>

<button class="btn btn-info btn-block collapsed" data-toggle="collapse"
        data-target="#kbd_login">${_("kbd.cluster.login.title")}</button>
<!-- Login panel -->
<div id="kbd_login" class="kbd collapse">
    <div class="loading active"></div>
    <p>&hellip;</p>
</div>

<div id="student-panel-placeholder"></div>
</div>
